package org.example.sensor;

import org.example.core.WeatherData;
import org.example.core.exceptions.SensorException;

import java.time.Instant;
import java.util.Random;

public class SimulatedSensor implements Sensor {
    private final Random rnd = new Random();

    @Override
    public WeatherData read() throws SensorException {
        double t = 5 + rnd.nextDouble() * 25;
        double h = 20 + rnd.nextDouble() * 60;
        double p = 990 + rnd.nextDouble() * 40;
        return new WeatherData(t, h, p, Instant.now());
    }
}
