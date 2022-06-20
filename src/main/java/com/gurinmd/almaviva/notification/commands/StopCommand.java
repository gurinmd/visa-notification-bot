package com.gurinmd.almaviva.notification.commands;

import com.gurinmd.almaviva.notification.service.UserDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class StopCommand extends BotCommand {

    private static final String STOP_WATCHING_MESSAGE = "Отслеживание статуса остановлено";
    
    public StopCommand() {
        super("stop", "Останавливает слежение по всем данным, " +
            "связанным с текущим пользователем");
    }

    @Autowired
    private UserDataService userDataService;

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        try {
            log.info("Stopped for user {}", user.getId());
            userDataService.deleteUserData(user.getId());
            notifyWatchStopped(absSender, chat);
        } catch (TelegramApiException telegramApiException) {
            log.warn("Cannot handle /stop command!", telegramApiException);
        }
    }
    
    private void notifyWatchStopped(AbsSender absSender, Chat chat) throws TelegramApiException {
        absSender.execute(SendMessage.builder()
            .chatId(chat.getId().toString())
            .text(STOP_WATCHING_MESSAGE).build());        
    }
}
