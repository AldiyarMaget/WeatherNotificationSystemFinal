package org.example.notificationsStrategy;

import org.example.core.WeatherData;
import org.example.core.WeatherStation;
import org.example.core.exceptions.SensorException;

import java.io.IOException;
import java.util.List;

public abstract class UpdateStrategy {
    protected volatile boolean running = false;
    protected Thread thread;

    public abstract long getIntervalMillis();
    public abstract String name();

    public void start(WeatherStation station) {
        running = true;
        thread = new Thread(() -> {
            while (running) {
                try {
                    List<WeatherData> list = station.readSensor();
                    for (WeatherData wd : list) {
                        station.publish(wd);
                    }
                } catch (IOException | SensorException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(getIntervalMillis());
                } catch (InterruptedException ignored) {}
            }
        });
        thread.start();
    }

    public void stop() {
        running = false;
        if (thread != null) thread.interrupt();
    }
}
