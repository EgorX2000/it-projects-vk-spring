package jobrecommender.telegram;

import jakarta.annotation.PreDestroy;
import jobrecommender.service.StringCommandsProcessingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramConfig {
    private BotSession botSession;

    @Bean
    public TelegramBot registerBot(@Value("${telegram.bot.name}") String botName, @Value("${telegram.bot.token}") String botToken, StringCommandsProcessingService commandsProcessor) {
        DefaultBotOptions botOptions = new DefaultBotOptions();
        botOptions.setProxyHost("127.0.0.1");
        botOptions.setProxyPort(10808);
        botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);

        TelegramBot telegramBot = new TelegramBot(botOptions, botToken, botName, commandsProcessor);

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botSession = botsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        return telegramBot;
    }

    @PreDestroy
    private void shutdownBot() {
        if (botSession != null && botSession.isRunning()) {
            botSession.stop();
        }
    }
}
