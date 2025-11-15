package org.example.telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
        private Map<String, Subscription> subscriptions = new HashMap<>();
        private int subscriptionIdCounter = 1;

        // Состояния пользователя
        enum UserState {
            START,
            WEATHER_PERIOD,
            WEATHER_STATION,
            WEATHER_CITY,
            SUBSCRIPTION_MENU,
            SUBSCRIPTION_TYPE,
            SUBSCRIPTION_CITY,
            UNSUBSCRIBE
        }

        // Временные данные для запроса погоды
        static class WeatherRequestData {
            String period;
            String station;
            String city;
        }

        // Класс подписки
        static class Subscription {
            int id;
            long userId;
            String type;
            String city;

            Subscription(int id, long userId, String type, String city) {
                this.id = id;
                this.userId = userId;
                this.type = type;
                this.city = city;
            }
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
                case SUBSCRIPTION_CITY:
                    handleSubscriptionCity(chatId, messageText);
                    break;
                case UNSUBSCRIBE:
                    handleUnsubscribe(chatId, messageText);
                    break;
                default:
                    if (messageText.equals("/start")) {
                        sendMainMenu(chatId);
                    }
                    break;
            }
        }

        private void handleCallbackQuery(Update update) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            int messageId = update.getCallbackQuery().getMessage().getMessageId();

            if (callbackData.startsWith("weather_")) {
                handleWeatherPeriod(chatId, callbackData.substring(8));
            } else if (callbackData.startsWith("station_")) {
                handleWeatherStation(chatId, callbackData.substring(8));
            } else if (callbackData.equals("subscription")) {
                showSubscriptionMenu(chatId);
            } else if (callbackData.equals("subscribe")) {
                showSubscriptionTypes(chatId);
            } else if (callbackData.equals("unsubscribe")) {
                showUnsubscribeList(chatId);
            } else if (callbackData.startsWith("subtype_")) {
                handleSubscriptionType(chatId, callbackData.substring(8));
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
            String type = "";
            int requestPeriod = 0;

            switch (period) {
                case "current":
                    type = "current";
                    requestPeriod = 0;
                    break;
                case "hour":
                    type = "hour";
                    requestPeriod = 3;
                    break;
                case "day":
                    type = "day";
                    requestPeriod = 1;
                    break;
                case "5days":
                    type = "day";
                    requestPeriod = 5;
                    break;
            }

            WeatherRequestData data = tempData.get(chatId);
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

        private void handleWeatherCity(long chatId, String city) {
            WeatherRequestData data = tempData.get(chatId);
            data.city = city;

            // Формируем запрос
            String type = "";
            int period = 0;

            switch (data.period) {
                case "current":
                    type = "current";
                    period = 0;
                    break;
                case "hour":
                    type = "hour";
                    period = 3;
                    break;
                case "day":
                    type = "day";
                    period = 1;
                    break;
                case "5days":
                    type = "day";
                    period = 5;
                    break;
            }

            String request = String.format("""
                {
                  "city": "%s",
                  "type": "%s",
                  "period": %d,
                  "station": "%s"
                }
                """, city, type, period, data.station);

            // Отправляем запрос
            //Send.sendRequest(request);

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Запрос отправлен: " + request);

            userStates.put(chatId, UserState.START);
            tempData.remove(chatId);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        private void showSubscriptionMenu(long chatId) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Подписка и отписка");

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(createButton("Subscribe", "subscribe"));
            row.add(createButton("Unsubscribe", "unsubscribe"));

            rows.add(row);
            markup.setKeyboard(rows);
            message.setReplyMarkup(markup);

            userStates.put(chatId, UserState.SUBSCRIPTION_MENU);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        private void showSubscriptionTypes(long chatId) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Выберите тип подписки:");

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(createButton("Ежечасный", "subtype_hourly"));
            row.add(createButton("Ежедневный", "subtype_daily"));
            row.add(createButton("Еженедельный", "subtype_weekly"));

            rows.add(row);
            markup.setKeyboard(rows);
            message.setReplyMarkup(markup);

            userStates.put(chatId, UserState.SUBSCRIPTION_TYPE);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        private void handleSubscriptionType(long chatId, String type) {
            String typeName = "";
            switch (type) {
                case "hourly":
                    typeName = "ежечасный";
                    break;
                case "daily":
                    typeName = "ежедневный";
                    break;
                case "weekly":
                    typeName = "еженедельный";
                    break;
            }

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Напишите название города для " + typeName + " подписки:");

            // Сохраняем тип подписки во временные данные
            WeatherRequestData data = new WeatherRequestData();
            data.period = type; // используем period поле для хранения типа подписки
            tempData.put(chatId, data);

            userStates.put(chatId, UserState.SUBSCRIPTION_CITY);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        private void handleSubscriptionCity(long chatId, String city) {
            WeatherRequestData data = tempData.get(chatId);
            String subscriptionType = data.period;

            // Создаем подписку
            int id = subscriptionIdCounter++;
            Subscription subscription = new Subscription(id, chatId, subscriptionType, city);
            subscriptions.put(String.valueOf(id), subscription);

            // Вызываем соответствующий метод
            switch (subscriptionType) {
                case "hourly":
                    everyhour(city);
                    break;
                case "daily":
                    everyday(city);
                    break;
                case "weekly":
                    everyweek(city);
                    break;
            }

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Подписка создана! ID: " + id + " (" + getSubscriptionTypeName(subscriptionType) + " - " + city + ")");

            userStates.put(chatId, UserState.START);
            tempData.remove(chatId);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        private void showUnsubscribeList(long chatId) {
            StringBuilder messageText = new StringBuilder("Ваши подписки:\n\n");

            boolean hasSubscriptions = false;
            for (Subscription sub : subscriptions.values()) {
                if (sub.userId == chatId) {
                    hasSubscriptions = true;
                    messageText.append("ID: ").append(sub.id)
                            .append(" - ").append(getSubscriptionTypeName(sub.type))
                            .append(" - ").append(sub.city).append("\n");
                }
            }

            if (!hasSubscriptions) {
                messageText.append("У вас нет активных подписок");
            } else {
                messageText.append("\nВведите ID подписки для отписки:");
            }

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(messageText.toString());

            userStates.put(chatId, UserState.UNSUBSCRIBE);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        private void handleUnsubscribe(long chatId, String input) {
            try {
                int id = Integer.parseInt(input.trim());
                Subscription subscription = subscriptions.get(input.trim());

                if (subscription != null && subscription.userId == chatId) {
                    subscriptions.remove(input.trim());

                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("Подписка ID: " + id + " отменена");

                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("Подписка с ID " + id + " не найдена");

                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            } catch (NumberFormatException e) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Пожалуйста, введите корректный ID подписки");

                try {
                    execute(message);
                } catch (TelegramApiException ex) {
                    ex.printStackTrace();
                }
            }

            userStates.put(chatId, UserState.START);
        }

        // Вспомогательные методы
        private InlineKeyboardButton createButton(String text, String callbackData) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(text);
            button.setCallbackData(callbackData);
            return button;
        }

        private String getSubscriptionTypeName(String type) {
            switch (type) {
                case "hourly": return "ежечасный";
                case "daily": return "ежедневный";
                case "weekly": return "еженедельный";
                default: return type;
            }
        }

        // Методы подписки (заглушки)
        private void everyhour(String city) {
            System.out.println("Ежечасная подписка для города: " + city);
            // Реализация ежечасной подписки
        }

        private void everyday(String city) {
            System.out.println("Ежедневная подписка для города: " + city);
            // Реализация ежедневной подписки
        }

        private void everyweek(String city) {
            System.out.println("Еженедельная подписка для города: " + city);
            // Реализация еженедельной подписки
        }
}
