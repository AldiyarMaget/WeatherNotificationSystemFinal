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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.util.*;

public class Bot extends TelegramLongPollingBot {

    private Map<Long, UserState> userStates = new HashMap<>();
    private Map<Long, WeatherRequestData> tempData = new HashMap<>();

    enum UserState {
        START,
        WEATHER_PERIOD,
        WEATHER_CITY
    }

    static class WeatherRequestData {
        String period;
        String city;
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
            handleMessage(update);
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);
        }
    }

    private void handleMessage(Update update) {
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
                        sendMessage(chatId, "stfu");
                        break;
                    default:
                        sendMessage(chatId, "sybau nigga");
                        sendMainMenu(chatId);
                        break;
                }
        }
    }

    private void handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (callbackData.startsWith("weather_")) {
            handleWeatherPeriod(chatId, callbackData.substring(8));
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

        sendMessage(message);
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
        row2.add(createButton("Today", "weather_today"));
        row2.add(createButton("Tomorrow", "weather_tomorrow"));
        row2.add(createButton("Week", "weather_week"));

        rows.add(row1);
        rows.add(row2);

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        userStates.put(chatId, UserState.WEATHER_PERIOD);
        tempData.put(chatId, new WeatherRequestData());

        sendMessage(message);
    }

    private void handleWeatherPeriod(long chatId, String period) {
        WeatherRequestData data = tempData.get(chatId);
        if (data == null) return;

        data.period = period;

        sendMessage(chatId, "Введите название города:");

        userStates.put(chatId, UserState.WEATHER_CITY);
    }

    private Sensor getSensorByPeriod(String period) {
        return switch (period) {
            case "current" -> new GoogleWeatherCurrentSensor();
            case "hour" -> new GoogleWeatherHourSensor();
            case "today" -> new GoogleWeatherTodaySensor();
            case "tomorrow" -> new GoogleWeatherTomorrowSensor();
            case "week" -> new GoogleWeatherWeeklySensor();
            default -> new GoogleWeatherCurrentSensor();
        };
    }

    private void handleWeatherCity(long chatId, String city) {
        WeatherRequestData data = tempData.get(chatId);
        if (data == null) return;

        data.city = city;

        Sensor sensor = getSensorByPeriod(data.period);

        try {
            List<WeatherData> weatherList = sensor.read();
            for (WeatherData wd : weatherList) {
                sendMessage(chatId, wd.toString());
            }
        } catch (IOException | SensorException e) {
            sendMessage(chatId, "Ошибка при чтении данных погоды.");
            e.printStackTrace();
        }

        userStates.put(chatId, UserState.START);
        tempData.remove(chatId);
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    private void sendWeatherData(long chatId, WeatherData wd) {
        StringBuilder sb = new StringBuilder();
        sb.append("🌤 Погода в городе: ").append(wd.city != null ? wd.city : "не указано").append("\n");
        sb.append("Температура: ").append(wd.temperature).append("°C").append("\n");
        sb.append("Ощущается как: ").append(wd.feelsLike).append("°C").append("\n");
        sb.append("Мин/Макс: ").append(wd.minTemperature).append("°C / ").append(wd.maxTemperature).append("°C").append("\n");
        sb.append("Влажность: ").append(wd.humidity).append("%").append("\n");
        sb.append("Облачность: ").append(wd.cloudCover).append("%").append("\n");
        sb.append("Ветер: ").append(wd.windSpeed).append(" м/с, ").append(wd.windDirection).append("\n");
        if (wd.sunrise != null && !wd.sunrise.isEmpty())
            sb.append("Восход: ").append(wd.sunrise).append("\n");
        if (wd.sunset != null && !wd.sunset.isEmpty())
            sb.append("Закат: ").append(wd.sunset).append("\n");
        sb.append("Описание: ").append(wd.description).append("\n");

        sendMessage(chatId, sb.toString());
    }


    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        sendMessage(message);
    }

    private void sendMessage(SendMessage message) {
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
