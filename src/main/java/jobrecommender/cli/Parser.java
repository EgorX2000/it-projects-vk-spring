package jobrecommender.cli;

import jobrecommender.service.LogsService;
import jobrecommender.service.StringCommandsHandlerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

@Component
@Profile("!test")
public class Parser implements CommandLineRunner {
    private final ApplicationContext applicationContext;
    private final Scanner scanner;
    private final LogsService commandLogger;
    private final Map<String, Consumer<String[]>> commandsMap;

    private boolean isRunning = true;

    public Parser(ApplicationContext applicationContext, Scanner scanner, LogsService commandLogger, StringCommandsHandlerService commandsHandler) {
        this.applicationContext = applicationContext;
        this.scanner = scanner;
        this.commandLogger = commandLogger;

        this.commandsMap = Map.of(
                "user", commandsHandler::handleUser,
                "job", commandsHandler::handleJob,
                "user-list", args -> commandsHandler.handleUserList().forEach(System.out::println),
                "job-list", args -> commandsHandler.handleJobList().forEach(System.out::println),
                "suggest", subcommands -> commandsHandler.handleSuggestions(subcommands).forEach(System.out::println),
                "stat", subcommands -> commandsHandler.handleStatistics(subcommands).stream().map(Object::toString).forEach(System.out::println),
                "history", args -> commandsHandler.handleHistory().forEach(System.out::println),
                "exit", args -> handleExit()
        );
    }

    private void parseCommands() {
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

    private void parseHistoryCommands() {
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
