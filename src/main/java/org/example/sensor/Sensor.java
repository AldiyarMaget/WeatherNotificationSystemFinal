package org.example.sensor;

import org.example.core.WeatherData;
import org.example.core.exceptions.SensorException;

public interface Sensor {
    WeatherData read() throws SensorException;
}
