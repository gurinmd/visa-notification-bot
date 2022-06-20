package com.gurinmd.almaviva.notification.commands;

import java.util.List;

import com.gurinmd.almaviva.notification.entity.UserData;
import com.gurinmd.almaviva.notification.service.UserDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class StatsCommand extends BotCommand {

    private static final String EMPTY_DATA_MESSAGE = "Отслеживание не выполняется";
    private static final String HEADER_MESSAGE = "Выполняется отслеживание по следующим документам:";
    private static final String SINGLE_ENTITY_MASK = "Паспорт *%s* рег. номер *%s*";
    
    @Autowired
    private UserDataService userDataService;
    
    public StatsCommand() {
        super("stats", "Показать все данные, по которым текущий " +
            "пользователь выполняет слежение");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        try {
            List<UserData> userDataList = userDataService.findByUserId(user.getId());
            String message = buildMessage(userDataList);
            sendMessageToUser(absSender, message, chat.getId());
        } catch (TelegramApiException telegramApiException) {
            log.error("Error handling /stats command", telegramApiException);
        }
    }
    
    private String buildMessage(List<UserData> userDataList) {
        String res;
        if (!CollectionUtils.isEmpty(userDataList)) {
            StringBuilder stringBuilder = new StringBuilder(HEADER_MESSAGE)
                .append("\n");
            for (UserData userData : userDataList) {
                String currentEntityString = String.format(SINGLE_ENTITY_MASK,
                    userData.getPassport(), userData.getFolder());
                stringBuilder.append(currentEntityString).append("\n");
            }
            res = stringBuilder.toString();
        } else {
            res = EMPTY_DATA_MESSAGE;
        }
        return res;
    }
    
    private void sendMessageToUser(AbsSender absSender, String message, Long chatId) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
            .chatId(String.valueOf(chatId))
            .text(message)
            .parseMode(ParseMode.MARKDOWN)
            .build();
        absSender.execute(sendMessage);
    }
}
