package org.example.core;

import org.example.core.exceptions.SensorException;
import org.example.observer.Observer;
import org.example.observer.Observed;
import org.example.sensor.Sensor;
import org.example.strategy.UpdateStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WeatherStation implements Observed {
    private final List<Observer> subscribers = new ArrayList<>();
    private volatile WeatherData lastWeatherData;
    private volatile UpdateStrategy currentStrategy;
    private final Object strategyLock = new Object();
    private Sensor sensor;
    private final List<Observer> observers = new ArrayList<>();

    @Override
    public void addSubscriber(Observer subscriber) {
        if (subscriber == null) return;
        synchronized (subscribers) {
            if (!subscribers.contains(subscriber)) subscribers.add(subscriber);
        }
        WeatherData snapshot = lastWeatherData;
        if (snapshot != null) {
            Thread t = new Thread(() -> safeUpdate(subscriber, snapshot),
                    "notify-on-attach-" + subscriber.getClass().getSimpleName());
            t.setDaemon(true);
            t.start();
        }
    }

    @Override
    public void removeSubscriber(Observer subscriber) {
        synchronized (subscribers) {
            subscribers.remove(subscriber);
        }
    }

    @Override
    public void publish(WeatherData data) {
        if (data == null) return;
        lastWeatherData = data;
        System.out.println("[WeatherStation] published: " + data);
        notifySubscribers();
    }

    private void notifySubscribers() {
        List<Observer> snapshot;
        synchronized (subscribers) {
            snapshot = new ArrayList<>(subscribers);
        }
        if (snapshot.isEmpty()) return;
        final WeatherData data = lastWeatherData;
        for (Observer o : snapshot) {
            Thread t = new Thread(() -> safeUpdate(o, data),
                    "notify-" + o.getClass().getSimpleName() + "-" + Math.abs(o.hashCode() % 1000));
            t.setDaemon(true);
            t.start();
        }
    }

    private void safeUpdate(Observer o, WeatherData d) {
        try { o.update(d); } catch (Exception ignored) {}
    }

    public void setStrategy(UpdateStrategy next) {
        synchronized (strategyLock) {
            if (currentStrategy != null) {
                System.out.println("[WeatherStation] stopping strategy: " + currentStrategy.name());
                currentStrategy.stop();
            }
            currentStrategy = next;
            if (currentStrategy != null) {
                System.out.println("[WeatherStation] starting strategy: " + currentStrategy.name());
                try {
                    currentStrategy.start(this);
                } catch (SensorException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public UpdateStrategy getCurrentStrategy() {
        return currentStrategy;
    }

    public void shutdown() {
        synchronized (strategyLock) {
            if (currentStrategy != null) currentStrategy.stop();
        }
        System.out.println("[WeatherStation] shutdown complete");
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public List<WeatherData> readSensor(String city, String period) throws IOException, SensorException {
        if (sensor == null) throw new IOException("Sensor not set");
        return sensor.read();
    }

    public void addObserver(Observer o) {
        synchronized (observers) {
            observers.add(o);
        }
    }

    public void removeObserver(Observer o) {
        synchronized (observers) {
            observers.remove(o);
        }
    }
}
