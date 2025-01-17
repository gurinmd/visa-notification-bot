package com.gurinmd.almaviva.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VisaNotificationBotApplication {

	public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(VisaNotificationBotApplication.class, args);
	}

}
