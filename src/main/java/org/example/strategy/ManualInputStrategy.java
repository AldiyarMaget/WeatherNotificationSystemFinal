package org.example.strategy;

import org.example.core.WeatherData;
import org.example.core.WeatherStation;
import org.example.core.exceptions.SensorException;
import org.example.sensor.Sensor;

import java.io.IOException;

public class ManualInputStrategy implements UpdateStrategy {
    private volatile WeatherStation station;
    private final Sensor sensor;

    public ManualInputStrategy(Sensor sensor) {
        this.sensor = sensor;
    }

    @Override
    public void start(WeatherStation station) throws SensorException, IOException {
        this.station = station;
        WeatherData data = sensor.read();
        station.publish(data);
    }

    @Override
    public void stop() {
        this.station = null;
    }

    @Override
    public String name() {
        return "manual";
    }
}
