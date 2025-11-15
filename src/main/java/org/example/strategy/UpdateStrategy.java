package org.example.strategy;

import org.example.core.WeatherStation;
import org.example.core.exceptions.SensorException;
import org.example.sensor.Sensor;

import java.io.IOException;

public interface UpdateStrategy {
    void start(WeatherStation station) throws SensorException, IOException;
    void stop();
    String name();
}
