package jobrecommender.cli;

import jobrecommender.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

@Component
@Profile("!test")
public class Parser implements CommandLineRunner {
    private final ApplicationContext applicationContext;
    private final Scanner scanner;
    private final UserService userService;
    private final JobService jobService;
    private final StatService statService;
    private final LogsService commandLogger;
    private final Map<String, Consumer<String[]>> commandsMap;

    private boolean isRunning = true;

    public Parser(ApplicationContext applicationContext, Scanner scanner, UserService userService, JobService jobService, SuggestService suggestService, LogsService commandLogger, StatService statService) {
        this.applicationContext = applicationContext;
        this.scanner = scanner;
        this.userService = userService;
        this.jobService = jobService;
        this.commandLogger = commandLogger;
        this.statService = statService;

        this.commandsMap = Map.of(
                "user", this::parseUser,
                "user-list", args -> userService.getUsers().values().forEach(System.out::println),
                "job", this::parseJob,
                "job-list", args -> jobService.getJobs().values().forEach(System.out::println),
                "suggest", subcommands -> suggestService.suggestJob(subcommands[1]).forEach(System.out::println),
                "history", args -> {
                    try {
                        this.parseHistory();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                "stat", this::parseStatistics,
                "exit", args -> handleExit()
        );
    }

    public void parseCommands() {
        while (isRunning && scanner.hasNextLine()) {
            String command = scanner.nextLine();
            String[] subcommands = command.split(" ");

            Consumer<String[]> commandHandler = commandsMap.get(subcommands[0]);
            if (commandHandler != null) {
                commandHandler.accept(subcommands);
                if (!subcommands[0].equals("exit")) {
                    try {
                        commandLogger.logCommand(command);
                    } catch (IOException e) {
                        System.out.println("Error while writing command log: " + e.getMessage());
                    }
                }
            } else {
                System.out.println("Invalid command!");
            }
        }
    }

    public void parseHistoryCommands() {
        List<String> commandLogs;
        try {
            commandLogs = commandLogger.getCommands();
        } catch (IOException e) {
            System.out.println("Error while parsing commands from file: " + e.getMessage());
            return;
        }

        for (String command : commandLogs) {
            String[] subcommands = command.split(" ");

            Consumer<String[]> commandHandler = commandsMap.get(subcommands[0]);
            if (commandHandler != null) {
                if (subcommands[0].equals("user") || subcommands[0].equals("job")) {
                    commandHandler.accept(subcommands);
                }
            } else {
                System.out.println("Invalid historical command!");
            }
        }
    }

    private void parseUser(String[] subcommands) {
        String name = subcommands[1];
        Set<String> skills = new HashSet<>();
        int experience = 0;

        for (int i = 2; i < subcommands.length; i++) {
            if (subcommands[i].startsWith("--skills=")) {
                skills.addAll(List.of(subcommands[i].substring("--skills=".length()).split(",")));
            } else if (subcommands[i].startsWith("--exp=")) {
                experience = Integer.parseInt(subcommands[i].substring("--exp=".length()));
            }
        }

        userService.addUser(name, skills, experience);
    }

    private void parseJob(String[] subcommands) {
        String title = subcommands[1];
        String company = "";
        Set<String> tags = new HashSet<>();
        int requiredExperience = 0;

        for (int i = 2; i < subcommands.length; i++) {
            if (subcommands[i].startsWith("--company=")) {
                company = subcommands[i].substring("--company=".length());
            } else if (subcommands[i].startsWith("--tags=")) {
                tags.addAll(List.of(subcommands[i].substring("--tags=".length()).split(",")));
            } else if (subcommands[i].startsWith("--exp=")) {
                requiredExperience = Integer.parseInt(subcommands[i].substring("--exp=".length()));
            }
        }

        jobService.addJob(title, company, tags, requiredExperience);
    }

    private void parseHistory() throws IOException {
        List<String> commandLogs = commandLogger.getCommands();
        commandLogs.forEach(System.out::println);
    }

    private void parseStatistics(String[] subcommands) {
        switch (subcommands[1]) {
            case "--exp":
                if (subcommands.length > 2) {
                    statService.jobsByExperience(Integer.parseInt(subcommands[2])).forEach(System.out::println);
                }
                break;
            case "--match":
                if (subcommands.length > 2) {
                    statService.usersByMatches(Integer.parseInt(subcommands[2])).forEach(System.out::println);
                }
                break;
            case "--top-skills":
                if (subcommands.length > 2) {
                    statService.topSkills(Integer.parseInt(subcommands[2])).forEach(System.out::println);
                }
                break;
        }
    }

    private void handleExit() {
        isRunning = false;

        SpringApplication.exit(applicationContext);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            parseHistoryCommands();
            parseCommands();
        } catch (Exception e) {
            if (isRunning) {
                System.out.println("Error: " + e.getMessage());
                handleExit();
            }
        }
    }
}
