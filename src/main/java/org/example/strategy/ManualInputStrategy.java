package org.example.strategy;

import org.example.core.WeatherData;
import org.example.core.WeatherStation;
import org.example.core.exceptions.SensorException;
import org.example.sensor.Sensor;

import java.io.IOException;
import java.util.List;

public class ManualInputStrategy implements UpdateStrategy {
    private volatile WeatherStation station;
    private final Sensor sensor;
    private final String city;

    public ManualInputStrategy(Sensor sensor, String city) {
        this.sensor = sensor;
        this.city = city;
    }

    @Override
    public void start(WeatherStation station) throws SensorException, IOException {
        this.station = station;
        List<WeatherData> data = sensor.read(city);
        if (data != null) {
            for (WeatherData weatherData : data) {
                station.publish(weatherData);
            }
        }
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
