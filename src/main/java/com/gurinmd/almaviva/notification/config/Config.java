package com.gurinmd.almaviva.notification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.HelpCommand;

@Configuration
public class Config {
    
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public HelpCommand helpCommand() {
        return new HelpCommand();
    }
}
