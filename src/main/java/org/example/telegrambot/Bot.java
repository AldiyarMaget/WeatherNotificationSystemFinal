package org.example.telegrambot;

import org.example.core.WeatherData;
import org.example.core.exceptions.SensorException;
import org.example.sensor.*;
import org.example.subscribers.Subscription;
import org.example.subscribers.SubscriptionDB;
import org.example.subscribers.SubscriptionManager;
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
        WEATHER_CITY,
        SUBSCRIBE_CITY,
        UNSUBSCRIBE_ID
    }

    static class TempData {
        String period; // для Weather
        String interval; // для Subscription
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
            } catch (SensorException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
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
            case SUBSCRIBE_CITY:
                handleSubscribeCityInput(chatId, text);
                break;
            case UNSUBSCRIBE_ID:
                handleUnsubscribeIdInput(chatId, text);
                break;
            case START:
            default:
                if ("/start".equals(text)) {
                    sendMainMenu(chatId);
                } else if ("Weather".equals(text)) {
                    showWeatherMenu(chatId);
                } else if ("Subscription".equals(text)) {
                    showSubscriptionMenu(chatId);
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
            tempData.get(chatId).period = callbackData.substring(8); // current, hour, today, tomorrow, week
            sendMessage(chatId, "Введите город для запроса:");
            userStates.put(chatId, UserState.WEATHER_CITY);

        } else if (callbackData.startsWith("sub_")) {
            switch (callbackData) {
                case "sub_subscribe" -> showSubscribeIntervalMenu(chatId);
                case "sub_unsubscribe" -> showUnsubscribeList(chatId);
            }
        } else if (callbackData.startsWith("interval_")) {
            String interval = callbackData.substring(9); // hour, day, week
            tempData.put(chatId, new TempData());
            tempData.get(chatId).interval = interval;
            sendMessage(chatId, "Введите город для подписки:");
            userStates.put(chatId, UserState.SUBSCRIBE_CITY);
        }
    }

    private void sendMainMenu(long chatId) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText("Добро пожаловать! Выберите опцию:");

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        row.add("Weather");
        row.add("Subscription");

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

    private String formatWeatherData(Object wd) {
        return wd.toString();
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText(text);
        btn.setCallbackData(callbackData);
        return btn;
    }

    private final SubscriptionDB subscriptionDB = new SubscriptionDB();

    private void showSubscriptionMenu(long chatId) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText("Выберите действие:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton("Subscribe", "sub_subscribe"));
        row.add(createButton("Unsubscribe", "sub_unsubscribe"));
        rows.add(row);
        markup.setKeyboard(rows);
        msg.setReplyMarkup(markup);

        try { execute(msg); } catch (TelegramApiException e) { e.printStackTrace(); }
    }

    private void showSubscribeIntervalMenu(long chatId) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText("Выберите интервал подписки:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton("Every Hour", "interval_hour"));
        row.add(createButton("Every Day", "interval_day"));
        row.add(createButton("Every Week", "interval_week"));
        rows.add(row);
        markup.setKeyboard(rows);
        msg.setReplyMarkup(markup);

        try { execute(msg); } catch (TelegramApiException e) { e.printStackTrace(); }
    }

    private void handleSubscribeCityInput(long chatId, String city) {
        TempData data = tempData.get(chatId);
        if (data == null) return;

        subscriptionDB.addSubscription(chatId, city, data.interval);

        sendMessage(chatId, "Подписка создана: город = " + city + ", интервал = " + data.interval);
        tempData.remove(chatId);
        userStates.put(chatId, UserState.START);
    }

    private void showUnsubscribeList(long chatId) {
        List<SubscriptionDB.Subscription> list = subscriptionDB.getSubscriptions(chatId); // только БД
        if (list.isEmpty()) {
            sendMessage(chatId, "У вас нет подписок");
            return;
        }
        StringBuilder sb = new StringBuilder("Ваши подписки:\n");
        for (SubscriptionDB.Subscription s : list) {
            sb.append("ID: ").append(s.id)
                    .append(" | Город: ").append(s.city)
                    .append(" | Интервал: ").append(s.strategyType)
                    .append("\n");
        }
        sendMessage(chatId, sb.toString());
        sendMessage(chatId, "Введите ID подписки для удаления:");
        userStates.put(chatId, UserState.UNSUBSCRIBE_ID);
    }

    private void handleUnsubscribeIdInput(long chatId, String input) {
        try {
            int id = Integer.parseInt(input);

            List<SubscriptionDB.Subscription> list = subscriptionDB.getSubscriptions(chatId);
            boolean removed = list.stream().anyMatch(s -> {
                if (s.id == id) {
                    subscriptionDB.removeSubscription(id);
                    return true;
                }
                return false;
            });

            sendMessage(chatId, removed ? "Подписка удалена" : "Такой подписки нет");
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Введите корректный ID");
        }
        userStates.put(chatId, UserState.START);
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
