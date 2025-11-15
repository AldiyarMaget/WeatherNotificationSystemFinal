package org.example.telegrambot;

import org.example.RabbitMQ.Send;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.*;

public class Bot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "WeatehrNotificationsSDPbot";
    }

    @Override
    public String getBotToken() {
        return "8513493773:AAGWu0U0hlT5_fhRq7UQto_FtDav68K2UzQ";
    }

    private Map<Long, UserState> userStates = new HashMap<>();
    private Map<Long, WeatherRequestData> tempData = new HashMap<>();

    enum UserState {
        START,
        WEATHER_PERIOD,
        WEATHER_STATION,
        WEATHER_CITY
    }

    static class WeatherRequestData {
        String period;
        String station;
        String city;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                handleMessage(update);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);
        }
    }

    private void handleMessage(Update update) throws Exception {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        UserState currentState = userStates.getOrDefault(chatId, UserState.START);

        switch (currentState) {
            case WEATHER_CITY:
                handleWeatherCity(chatId, messageText);
                break;
            default:
                switch (messageText) {
                    case "/start":
                        sendMainMenu(chatId);
                        break;
                    case "Weather":
                        handleWeatherCommand(chatId);
                        break;
                    case "Subscription":
                        sendMessage(chatId, "Меню подписок пока не реализовано");
                        break;
                    default:
                        sendMessage(chatId, "Неизвестная команда. Используйте главное меню.");
                        sendMainMenu(chatId);
                        break;
                }
                break;
        }
    }

    private void handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (callbackData.startsWith("weather_")) {
            handleWeatherPeriod(chatId, callbackData.substring(8));
        } else if (callbackData.startsWith("station_")) {
            handleWeatherStation(chatId, callbackData.substring(8));
        }
    }

    private void sendMainMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Добро пожаловать! Выберите опцию:");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Weather");
        row1.add("Subscription");

        keyboard.add(row1);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);

        userStates.put(chatId, UserState.START);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleWeatherCommand(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите период:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createButton("Current", "weather_current"));
        row1.add(createButton("Next 3 hours", "weather_hour"));

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createButton("Tomorrow", "weather_day"));
        row2.add(createButton("Next 5 days", "weather_5days"));

        rows.add(row1);
        rows.add(row2);

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        userStates.put(chatId, UserState.WEATHER_PERIOD);
        tempData.put(chatId, new WeatherRequestData());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleWeatherPeriod(long chatId, String period) {
        WeatherRequestData data = tempData.get(chatId);
        if (data == null) return;

        data.period = period;

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите станцию:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton("Google Weather", "station_google"));
        row.add(createButton("WeatherAPI", "station_weatherapi"));
        row.add(createButton("OpenWeather", "station_openweather"));

        rows.add(row);
        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        userStates.put(chatId, UserState.WEATHER_STATION);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleWeatherStation(long chatId, String station) {
        WeatherRequestData data = tempData.get(chatId);
        if (data == null) return;

        data.station = station;

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Напишите название города:");

        userStates.put(chatId, UserState.WEATHER_CITY);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleWeatherCity(long chatId, String city) throws Exception {
        WeatherRequestData data = tempData.get(chatId);
        if (data == null) return;

        data.city = city;

        String type = switch (data.period) {
            case "current" -> "current";
            case "hour" -> "hour";
            case "day" -> "day";
            case "5days" -> "day";
            default -> "current";
        };

        int period = switch (data.period) {
            case "current" -> 0;
            case "hour" -> 3;
            case "day" -> 1;
            case "5days" -> 5;
            default -> 0;
        };

        String request = String.format("""
            {
              "city": "%s",
              "type": "%s",
              "period": %d,
              "station": "%s"
            }
            """, city, type, period, data.station);

        // Отправляем в RabbitMQ
        Send.sendRequest(request);

        sendMessage(chatId, "Запрос отправлен в RabbitMQ: " + request);

        userStates.put(chatId, UserState.START);
        tempData.remove(chatId);
    }


    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = new Bot();
        botsApi.registerBot(bot);
        System.out.println("Bot started");
    }
}
