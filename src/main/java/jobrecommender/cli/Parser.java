package jobrecommender.cli;

import jobrecommender.service.LogsService;
import jobrecommender.service.StringCommandsProcessingService;
import org.springframework.boot.CommandLineRunner;
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
    private final Scanner scanner;
    private final StringCommandsProcessingService commandProcessor;
    private final LogsService commandLogger;
    private final Map<String, Consumer<String[]>> commandsMap;

    public Parser(Scanner scanner, StringCommandsProcessingService commandProcessor, LogsService commandLogger) {
        this.scanner = scanner;
        this.commandProcessor = commandProcessor;
        this.commandLogger = commandLogger;

        this.commandsMap = Map.of(
                "user", commandProcessor::handleUser,
                "job", commandProcessor::handleJob,
                "user-list", args -> commandProcessor.handleUserList().forEach(System.out::println),
                "job-list", args -> commandProcessor.handleJobList().forEach(System.out::println),
                "suggest", subcommands -> commandProcessor.handleSuggestions(subcommands).forEach(System.out::println),
                "stat", subcommands -> commandProcessor.handleStatistics(subcommands).forEach(System.out::println),
                "history", args -> commandProcessor.handleHistory().forEach(System.out::println),
                "exit", args -> commandProcessor.handleExit()
        );
    }

    private void parseCommands() {
        while (commandProcessor.isRunning() && scanner.hasNextLine()) {
            String command = scanner.nextLine();
            String[] subcommands = command.split(" ");

            Consumer<String[]> commandHandler = commandsMap.get(subcommands[0]);
            if (commandHandler != null) {
                commandProcessor.processCommandAndLog(command, () -> commandHandler.accept(subcommands));
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

    @Override
    public void run(String... args) throws Exception {
        try {
            parseHistoryCommands();
            parseCommands();
        } catch (Exception e) {
            if (commandProcessor.isRunning()) {
                System.out.println("Error: " + e.getMessage());
                commandProcessor.handleExit();
            }
        }
    }
}
