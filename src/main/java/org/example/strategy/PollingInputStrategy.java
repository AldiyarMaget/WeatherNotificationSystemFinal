package org.example.strategy;

import org.example.core.WeatherData;
import org.example.core.WeatherStation;
import org.example.core.exceptions.SensorException;
import org.example.sensor.Sensor;

import java.io.IOException;
import java.util.List;
/*
public class PollingInputStrategy implements UpdateStrategy {
    private final Sensor sensor;
    private final long periodSeconds;
    private volatile boolean running = false;
    private Thread worker;
    private static final long MIN_PERIOD_SECONDS = 5;

    public PollingInputStrategy(Sensor sensor, long periodSeconds) {
        this.sensor = sensor;
        this.periodSeconds = Math.max(MIN_PERIOD_SECONDS, periodSeconds);
    }

    @Override
    public void start(WeatherStation station) {
        if (running) return;
        running = true;
        worker = new Thread(() -> {
            while (running) {
                try {
                    List<WeatherData> data = sensor.read();
                    station.publish(data);
                } catch (SensorException ex) {
                    System.out.println("[SensorPollingStrategy] sensor read error: " + ex.getMessage());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(periodSeconds * 1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "SensorPollingThread");
        worker.setDaemon(true);
        worker.start();
    }

    @Override
    public void stop() {
        running = false;
        if (worker != null) {
            worker.interrupt();
            try { worker.join(1000); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        }
    }

    @Override
    public String name() {
        return "sensor-polling";
    }
}
*/