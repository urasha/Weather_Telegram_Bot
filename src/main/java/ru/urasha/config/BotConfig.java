package ru.urasha.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public final class BotConfig {

    @Value("${bot-token}")
    private String telegramToken;

    @Value("${bot-name}")
    private String telegramName;
}
