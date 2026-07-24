package jobrecommender.telegram;

import jobrecommender.domain.Job;
import jobrecommender.domain.User;
import jobrecommender.service.StringCommandsProcessingService;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.function.Function;

public class TelegramBot extends TelegramLongPollingBot {
    private final String botName;
    private final StringCommandsProcessingService commandProcessor;
    private final Map<String, Function<String[], String>> commandsMap;

    public TelegramBot(DefaultBotOptions options, String botToken, String botName, StringCommandsProcessingService commandProcessor) {
        super(options, botToken);

        this.botName = botName;
        this.commandProcessor = commandProcessor;

        this.commandsMap = Map.of(
                "/start", args -> String.format(
                        """
                        <b>Available commands:</b>%n
                        <code>user &lt;name&gt; --skills=... --exp=...</code> - adds a user with the specified skills and word experience%n
                        <code>job &lt;title&gt; --company=... --tags=... --exp=...</code> - adds a job in the specified company, with the specified tags (required employee skills) and required work experience%n
                        <code>user-list</code> - displays a list of registered users%n
                        <code>job-list</code> - displays a list of available jobs%n
                        <code>suggest &lt;username&gt;</code> - displays no more than two job openings that match the specified user based on their skills and experience%n
                        <code>stat --exp &lt;N&gt;</code> - displays jobs with experience not less than specified%n
                        <code>stat --match &lt;N&gt;</code> - displays users who have more than or equal to N matches%n
                        <code>stat --top-skills &lt;N&gt;</code> - displays top N skills among all users (skills that are most frequently encountered)
                        
                        
                        <b>Example:</b>
                        <i>user alice --skills=java,ml,linux --exp=2
                        user bob --skills=java,java,java --exp=10
                        user-list</i>
                        """
//                        <code>history</code> - displays a list of all previously entered commands
//                        <code>/shutdown</code> - shutdowns backend application
                ),
                "user", subcommands -> {
                    commandProcessor.handleUser(subcommands);
                    return "Success!";
                },
                "job", subcommands -> {
                    commandProcessor.handleJob(subcommands);
                    return "Success!";
                },
                "user-list", args ->
                        String.join(String.format("%n"), commandProcessor.handleUserList().stream().map(User::toString).toList()),
                "job-list", args ->
                        String.join(String.format("%n"), commandProcessor.handleJobList().stream().map(Job::toString).toList()),
                "suggest", subcommands ->
                        String.join(String.format("%n"), commandProcessor.handleSuggestions(subcommands).stream().map(Job::toString).toList()),
                "stat", subcommands ->
                        String.join(String.format("%n"), commandProcessor.handleStatistics(subcommands))
//                "history", args -> String.join(String.format("%n"), commandProcessor.handleHistory()),
//                "/shutdown", args -> commandProcessor.handleExit()
        );
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message incomingMessage = update.getMessage();

        String[] subcommands = incomingMessage.getText().split(" ");
        Function<String[], String> commandHandler = commandsMap.get(subcommands[0]);
        if (commandHandler != null) {
            commandProcessor.processCommandAndLog(incomingMessage.getText(),
                    () -> sendResponse(incomingMessage.getChatId(), commandHandler.apply(subcommands)));
        } else {
            sendResponse(incomingMessage.getChatId(), "Invalid command!");
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    private void sendResponse(long chatId, String response) {
        try {
            SendMessage responseMessage = new SendMessage();

            responseMessage.setChatId(chatId);
            responseMessage.setText(response);
            responseMessage.setParseMode("HTML");

            execute(responseMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
