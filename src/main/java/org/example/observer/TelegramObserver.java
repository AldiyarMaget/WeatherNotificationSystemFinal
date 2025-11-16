package org.example.observer;

import org.example.core.WeatherData;
import org.example.telegrambot.Bot;

public class TelegramObserver implements Observer {
    private final long chatId;
    private final Bot bot;

    public TelegramObserver(long chatId, Bot bot) {
        this.chatId = chatId;
        this.bot = bot;
    }

    @Override
    public void update(WeatherData data) {
        String message = String.format(
                "Weather in %s:\nTemp: %.1f°C\nFeels like: %.1f°C\nHumidity: %.0f%%\nDescription: %s",
                data.city() != null ? data.city() : "unknown",
                data.temperature(),
                data.feelsLike(),
                data.humidity(),
                data.description() != null ? data.description() : "N/A"
        );
        bot.sendMessage(chatId, message);
    }
}
