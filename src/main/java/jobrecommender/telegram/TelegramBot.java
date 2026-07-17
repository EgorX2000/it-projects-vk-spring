package jobrecommender.telegram;

import jobrecommender.domain.Job;
import jobrecommender.domain.User;
import jobrecommender.service.StringCommandsHandlerService;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class TelegramBot extends TelegramLongPollingBot {
    private final String botName;
    private final Map<String, Consumer<String[]>> commandsMap;
    private long chatId;

    public TelegramBot(DefaultBotOptions options, String botToken, String botName, StringCommandsHandlerService commandsHandler) {
        super(options, botToken);
        this.botName = botName;
        this.commandsMap = Map.of(
                "user", commandsHandler::handleUser,
                "job", commandsHandler::handleJob,
                "user-list", args -> sendResponse(String.join(String.format("%n"), commandsHandler.handleUserList().stream().map(User::toString).toList())),
                "job-list", args -> sendResponse(String.join(String.format("%n"), commandsHandler.handleJobList().stream().map(Job::toString).toList())),
                "suggest", subcommands -> sendResponse(String.join(String.format("%n"), commandsHandler.handleSuggestions(subcommands).stream().map(Job::toString).toList())),
                "stat", subcommands -> sendResponse(String.join(String.format("%n"), commandsHandler.handleStatistics(subcommands).stream().map(Object::toString).toList()))
//                "history", args -> sendResponse(String.join(String.format("%n"), commandsHandler.handleHistory()))
        );
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message incomingMessage = update.getMessage();
        chatId = incomingMessage.getChatId();

        String[] subcommands = incomingMessage.getText().split(" ");
        Consumer<String[]> commandHandler = commandsMap.get(subcommands[0]);
        if (commandHandler != null) {
            commandHandler.accept(subcommands);
        } else if (Objects.equals(incomingMessage.getText(), "/start")) {
            sendResponse(String.format(
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
//                    <code>history</code> - displays a list of all previously entered commands
            ));
        } else {
            sendResponse("Invalid command!");
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    private void sendResponse(String response) {
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
