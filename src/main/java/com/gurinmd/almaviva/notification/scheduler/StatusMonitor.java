package com.gurinmd.almaviva.notification.scheduler;

import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.gurinmd.almaviva.notification.TelegramStatusBot;
import com.gurinmd.almaviva.notification.dto.CheckStatusResult;
import com.gurinmd.almaviva.notification.entity.UserData;
import com.gurinmd.almaviva.notification.service.UserDataService;
import com.gurinmd.almaviva.notification.service.WebPortalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Slf4j
public class StatusMonitor {
    
    @Autowired
    private UserDataService userDataService;
    @Autowired
    private WebPortalService webPortalService;
    @Autowired
    private TelegramStatusBot telegramStatusBot;
    
    @Scheduled(cron = "0 0/5 * * * *")
    public void startNotificationChecking() {
        Stream<UserData> userDataStream = StreamSupport.stream(userDataService.findAll().spliterator(), false);
        userDataStream.forEach(userData -> {
            CheckStatusResult status = webPortalService.getStatusResultData(userData.getPassport(), userData.getFolder());
            if (!Objects.equals(status.getStatusCode(), userData.getStatusCode())) {
                try {
                    telegramStatusBot.notifyAboutChanges(userData, status);
                    userData.setStatusCode(status.getStatusCode());
                    userData.setStatusDescription(status.getStatusDescription());
                    userDataService.save(userData);
                } catch (TelegramApiException api) {
                    log.error("Error notifying about changes!", api);
                }
            }
        });
    }
}
