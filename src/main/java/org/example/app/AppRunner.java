package org.example.app;

import org.example.facade.WeatherSystemFacade;
import org.example.telegrambot.Bot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class AppRunner {

    public static void main(String[] args) throws Exception {
        WeatherSystemFacade facade = new WeatherSystemFacade();
        facade.start();

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = new Bot();
        botsApi.registerBot(bot);
        System.out.println("Bot started");

    }
}
