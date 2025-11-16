package org.example.app;

import org.example.core.WeatherData;
import org.example.core.WeatherStation;
import org.example.notificationsStrategy.Context;
import org.example.notificationsStrategy.UpdateStrategy;
import org.example.subscribers.SubscriptionManager;

import java.io.IOException;
import java.util.List;

import org.example.core.exceptions.SensorException;

public class WeatherFacade {

    private final Context context = new Context();

    public List<WeatherData> getWeather(String city, String period) throws SensorException, IOException {
        WeatherStation station = new WeatherStation(); // дефолтный конструктор
        return station.readSensor(city, period);       // передаем city и period сюда
    }

    public void addSubscription(long chatId, String city, String interval) {
        // Сохраняем подписку в БД
        SubscriptionManager.addSubscription(chatId, city, interval);

        // Определяем стратегию по интервалу
        UpdateStrategy strategy = switch (interval) {
            case "minute" -> new org.example.notificationsStrategy.MinuteStrategy();
            case "hour" -> new org.example.notificationsStrategy.HourlyStrategy();
            case "day" -> new org.example.notificationsStrategy.DailyStrategy();
            case "week" -> new org.example.notificationsStrategy.WeeklyStrategy();
            default -> throw new IllegalArgumentException("Unknown interval: " + interval);
        };

        WeatherStation station = new WeatherStation();

        context.setStrategy(strategy, station, city, interval);
    }

    public List<org.example.subscribers.SubscriptionDB.Subscription> getSubscriptions(long chatId) {
        return SubscriptionManager.getSubscriptions(chatId);
    }

    public boolean removeSubscription(long chatId, int id) {
        return SubscriptionManager.removeSubscription(chatId, id);
    }
}
