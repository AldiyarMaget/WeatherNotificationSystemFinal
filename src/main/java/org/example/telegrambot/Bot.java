package org.example.telegrambot;

import org.example.core.WeatherData;
import org.example.core.exceptions.SensorException;
import org.example.sensor.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.util.*;

public class Bot extends TelegramLongPollingBot {

    private final Map<Long, UserState> userStates = new HashMap<>();
    private final Map<Long, TempData> tempData = new HashMap<>();

    enum UserState {
        START,
        WEATHER_CITY
    }

    static class TempData {
        String period; // для Weather
    }

    @Override
    public String getBotUsername() {
        return "WeatehrNotificationsSDPbot";
    }

    @Override
    public String getBotToken() {
        return "8513493773:AAGWu0U0hlT5_fhRq7UQto_FtDav68K2UzQ";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                handleMessage(update.getMessage().getChatId(), update.getMessage().getText());
            } catch (SensorException | IOException e) {
                throw new RuntimeException(e);
            }
        } else if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery().getMessage().getChatId(),
                    update.getCallbackQuery().getData());
        }
    }

    private void handleMessage(long chatId, String text) throws SensorException, IOException {
        UserState state = userStates.getOrDefault(chatId, UserState.START);

        switch (state) {
            case WEATHER_CITY:
                handleWeatherCityInput(chatId, text);
                break;
            case START:
            default:
                if ("/start".equals(text)) {
                    sendMainMenu(chatId);
                } else if ("Weather".equals(text)) {
                    showWeatherMenu(chatId);
                } else {
                    sendMessage(chatId, "Неизвестная команда. Используйте главное меню.");
                    sendMainMenu(chatId);
                }
                break;
        }
    }

    private void handleCallback(long chatId, String callbackData) {
        if (callbackData.startsWith("weather_")) {
            tempData.put(chatId, new TempData());
            tempData.get(chatId).period = callbackData.substring(8);
            sendMessage(chatId, "Введите город для запроса:");
            userStates.put(chatId, UserState.WEATHER_CITY);
        }
    }

    private void sendMainMenu(long chatId) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText("Добро пожаловать! Выберите опцию:");

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        row.add("Weather");

        keyboard.setKeyboard(Collections.singletonList(row));
        keyboard.setResizeKeyboard(true);
        msg.setReplyMarkup(keyboard);

        userStates.put(chatId, UserState.START);

        try { execute(msg); } catch (TelegramApiException e) { e.printStackTrace(); }
    }

    private void showWeatherMenu(long chatId) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText("Выберите период:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createButton("Current", "weather_current"));
        row1.add(createButton("Next 3 hours", "weather_hour"));

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createButton("Today", "weather_today"));
        row2.add(createButton("Tomorrow", "weather_tomorrow"));
        row2.add(createButton("Week", "weather_week"));

        rows.add(row1);
        rows.add(row2);
        markup.setKeyboard(rows);

        msg.setReplyMarkup(markup);

        try { execute(msg); } catch (TelegramApiException e) { e.printStackTrace(); }
    }

    private void handleWeatherCityInput(long chatId, String city) throws SensorException, IOException {
        TempData data = tempData.get(chatId);
        if (data == null) return;

        String period = data.period;
        sendMessage(chatId, "Запрос отправлен: city=" + city + ", type=" + period);

        Sensor sensor = switch (data.period) {
            case "current" -> new GoogleWeatherCurrentSensor();
            case "today" -> new GoogleWeatherTodaySensor();
            case "tomorrow" -> new GoogleWeatherTomorrowSensor();
            case "week" -> new GoogleWeatherWeeklySensor();
            default -> throw new IllegalArgumentException("Unknown type: " + data.period);
        };

        List<WeatherData> dataList = sensor.read();
        for (WeatherData wd : dataList) {
            sendMessage(chatId, wd.toString());
        }

        tempData.remove(chatId);
        userStates.put(chatId, UserState.START);
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText(text);
        btn.setCallbackData(callbackData);
        return btn;
    }

    public void sendMessage(long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        try { execute(msg); } catch (TelegramApiException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = new Bot();
        botsApi.registerBot(bot);
        System.out.println("Bot started");
    }
}
