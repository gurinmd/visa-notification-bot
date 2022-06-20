package com.gurinmd.almaviva.notification.commands;

import com.gurinmd.almaviva.notification.service.UserDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class StartCommand extends BotCommand {
    
    private static final String INCORRECT_FORMAT_START_MESSAGE = "Команда /start должна быть в формате " +
        "\n\n /start XXXX ZZZZ\n\n " +
        "где XXXX - номер загранпаспорта (только цифры), ZZZZ - регистрационный номер (folder) (только цифры). " +
        "Пожалуйста, попробуйте снова";
    
    private static final String WATCH_STARTED_MESSAGE = "Включено отслеживание заявления по паспорту *%s* " +
        "и регистрационному номеру *%s*";

    public StartCommand() {
        super("start", "Команда запускает процесс отслеживания заявления. " +
            "Команда /start должна быть в формате " +
            "\n\n /start XXXX ZZZZ\n\n " +
            "где XXXX - номер загранпаспорта (только цифры), ZZZZ - регистрационный номер (folder) (только цифры).");
    }
    
    @Autowired
    private UserDataService userDataService;

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        try {
            if (!paramsExist(strings) || !paramFormatCorrect(strings)) {
                notifyWrongParamFormat(absSender, chat);
            } else {
                String passport = strings[0];
                String folder = strings[1];
                log.info("Added user {} - {} for passport {} and folder {}", user.getUserName(), 
                    user.getId(), passport, folder);
                userDataService.addUserData(user.getId(), user.getUserName(), chat.getId(), folder, passport);
                notifyWatchStarted(absSender, chat, passport, folder);
            }
        } catch (TelegramApiException telegramApiException) {
            log.warn("Cannot handle /start command!", telegramApiException);
        }
    }
    
    private boolean paramsExist(String[] params) {
        return params != null && params.length >= 2;
    }
    
    private boolean paramFormatCorrect(String[] params) {
        return params[0].matches("^\\d+$") && params[1].matches("^\\d+$");
    }
    
    
    private void notifyWrongParamFormat(AbsSender absSender, Chat chat) throws TelegramApiException {
        absSender.execute(SendMessage.builder()
            .chatId(chat.getId().toString())
            .text(INCORRECT_FORMAT_START_MESSAGE).build());
    }
    
    private void notifyWatchStarted(AbsSender absSender, Chat chat, String passport, String folder) throws TelegramApiException {
        String message = String.format(WATCH_STARTED_MESSAGE, passport, folder);
        absSender.execute(SendMessage.builder()
            .chatId(chat.getId().toString())
            .parseMode(ParseMode.MARKDOWN)
            .text(message).build());
    }
    
}
