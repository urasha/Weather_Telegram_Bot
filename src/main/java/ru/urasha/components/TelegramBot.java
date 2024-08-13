package ru.urasha.components;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.urasha.config.BotConfig;

import java.io.IOException;
import java.net.URISyntaxException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final String botName;
    private final WeatherDataCollector weatherCollector;

    @Value("${place-not-exist}")
    private String placeNotExistMessage;

    @Value("${unexpected-error}")
    private String unexpectedErrorMessage;

    @Value("${start-command}")
    private String startCommandMessage;

    @Autowired
    public TelegramBot(BotConfig botConfig, WeatherDataCollector weatherDataCollector) {
        super(botConfig.getTelegramToken());
        botName = botConfig.getTelegramName();
        weatherCollector = weatherDataCollector;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!(update.hasMessage() && update.getMessage().hasText())) {
            return;
        }

        SendMessage botMessage = new SendMessage();

        String resultMessage;
        if (update.getMessage().getText().equals("/start")) {
            resultMessage = handleStartCommand();
        } else {
            resultMessage = handleCity(update);
        }

        botMessage.setChatId(update.getMessage().getChatId());
        botMessage.setText(resultMessage);

        try {
            execute(botMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private String handleStartCommand() {
        return startCommandMessage;
    }

    private String handleCity(Update update) {
        try {
            JsonNode allParams = weatherCollector.getWeatherParams(update.getMessage().getText());
            JsonNode currentConditions = allParams.get("currentConditions");

            int temperature = convertFahrenheitToCelsius(currentConditions.get("temp").asDouble());
            int feelsLikeTemperature = convertFahrenheitToCelsius(currentConditions.get("feelslike").asDouble());
            int humidity = currentConditions.get("humidity").asInt();

            return String.format("Температура: %s°\nОщущается как: %s°\nВлажность: %s%%",
                    temperature, feelsLikeTemperature, humidity);
        } catch (URISyntaxException e) {
            return unexpectedErrorMessage;
        } catch (IOException | InterruptedException e) {
            return placeNotExistMessage;
        }

    }

    private int convertFahrenheitToCelsius(double fahrenheit) {
        return (int) Math.round((fahrenheit - 32) * 5 / 9);
    }
}
