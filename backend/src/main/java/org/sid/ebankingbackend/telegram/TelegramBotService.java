package org.sid.ebankingbackend.telegram;

import lombok.extern.slf4j.Slf4j;
import org.sid.ebankingbackend.agent.BankAiAgent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class TelegramBotService extends TelegramLongPollingBot {

    // In telegrambots 6.x, getBotToken() is final in DefaultAbsSender.
    // The token must be passed to super() — @Value is used to inject it from properties.
    private final String botUsername;
    private final BankAiAgent bankAiAgent;

    public TelegramBotService(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.name}") String botUsername,
            BankAiAgent bankAiAgent) {
        super(botToken);
        this.botUsername = botUsername;
        this.bankAiAgent = bankAiAgent;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        Long chatId = update.getMessage().getChatId();
        String userQuestion = update.getMessage().getText();
        String firstName = update.getMessage().getFrom().getFirstName();

        log.info("Message from {} (chatId={}): {}", firstName, chatId, userQuestion);

        sendTypingAction(chatId);

        try {
            // chatId is used as conversationId so each Telegram user has isolated memory
            String agentResponse = bankAiAgent.askAgent(userQuestion, chatId.toString());
            sendTextMessage(chatId, agentResponse);
        } catch (Exception e) {
            log.error("Error while calling BankAiAgent for chatId={}", chatId, e);
            sendTextMessage(chatId, "Sorry, I encountered an error while processing your request. Please try again.");
        }
    }

    private void sendTypingAction(Long chatId) {
        SendChatAction typingAction = SendChatAction.builder()
                .chatId(chatId.toString())
                .action(ActionType.TYPING.toString())
                .build();
        try {
            execute(typingAction);
        } catch (TelegramApiException e) {
            // Non-critical: typing indicator failure must never block the actual response
            log.warn("Could not send typing action to chatId={}: {}", chatId, e.getMessage());
        }
    }

    private void sendTextMessage(Long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Failed to send message to chatId={}: {}", chatId, e.getMessage());
        }
    }
}
