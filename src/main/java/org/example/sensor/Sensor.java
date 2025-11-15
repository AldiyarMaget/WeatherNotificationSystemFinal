package org.example.sensor;

import org.example.core.WeatherData;
import org.example.core.exceptions.SensorException;

import java.io.IOException;

@FunctionalInterface
public interface Sensor {
    WeatherData read() throws SensorException, IOException;
}
