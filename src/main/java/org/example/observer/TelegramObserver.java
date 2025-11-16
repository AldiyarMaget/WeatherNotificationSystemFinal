package org.example.observer;

import org.example.core.WeatherData;
import org.example.telegrambot.Bot;

import java.util.List;

public class TelegramObserver implements Observer {
    private final long chatId;
    private final Bot bot;

    public TelegramObserver(long chatId, Bot bot) {
        this.chatId = chatId;
        this.bot = bot;
    }

    @Override
    public void update(WeatherData data) {

    }


    @Override
    public void update(List<WeatherData> data) {

    }
}
