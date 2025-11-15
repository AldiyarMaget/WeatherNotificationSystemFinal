package org.example.sensor;

import org.example.core.WeatherData;
import org.example.core.exceptions.SensorException;

import java.io.IOException;
import java.util.List;

@FunctionalInterface
public interface Sensor {
    List<WeatherData> read() throws SensorException, IOException;
}
