package com.gurinmd.almaviva.notification;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

import com.gurinmd.almaviva.notification.commands.StartCommand;
import com.gurinmd.almaviva.notification.commands.StatsCommand;
import com.gurinmd.almaviva.notification.commands.StopCommand;
import com.gurinmd.almaviva.notification.dto.CheckStatusResult;
import com.gurinmd.almaviva.notification.entity.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.HelpCommand;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramStatusBot extends TelegramLongPollingCommandBot {

    private static final String NOTIFICATION_TEXT_TEMPLATE = "Статус заявления для паспорта %s " +
        "и регистрационного номера %s обновлен\\!\n*%s*";
    
    @Value("${telegram.bot.token}")
    private String token;
    
    @Autowired
    private StartCommand startCommand;
    
    @Autowired
    private StopCommand stopCommand;
    
    @Autowired
    private StatsCommand statsCommand;
    
    @PostConstruct
    public void initCommands() throws Exception{
        List<IBotCommand> commands = initCommandList();
        registerAll(commands.toArray(new IBotCommand[commands.size()]));
        setMyCommands(commands);
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        
    }

    @Override
    public String getBotUsername() {
        return "AlmavivaStatusBot";
    }
    
    public void notifyAboutChanges(UserData userData, 
                                   CheckStatusResult checkStatusResult) throws TelegramApiException {
        String text = String.format(NOTIFICATION_TEXT_TEMPLATE, checkStatusResult.getPassport(), 
            checkStatusResult.getFolder(), checkStatusResult.getStatusDescription());
        SendMessage sendMessage = SendMessage.builder()
            .chatId(userData.getChatId().toString())
            .text(text)
            .parseMode(ParseMode.MARKDOWNV2)
            .build();
        sendApiMethod(sendMessage);
    }
    
    private List<IBotCommand> initCommandList() {
        List<IBotCommand> commands = new ArrayList<>();
        commands.add(startCommand);
        commands.add(stopCommand);
        commands.add(statsCommand);
        commands.add(new HelpCommand());
        return commands;
    }
    
    private void setMyCommands(List<IBotCommand> commands) throws TelegramApiException {
        List<BotCommand> apiObjects = new ArrayList<>();
        commands.forEach(iBotCommand -> apiObjects.add(new BotCommand(iBotCommand.getCommandIdentifier(), 
            iBotCommand.getDescription())));
        SetMyCommands setMyCommandsRequest = SetMyCommands.builder()
            .commands(apiObjects)
            .build();
        sendApiMethod(setMyCommandsRequest);
    }
}
