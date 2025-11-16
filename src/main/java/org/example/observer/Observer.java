package org.example.observer;

import org.example.core.WeatherData;

import java.util.List;

public interface Observer {
    void update(List<WeatherData> data);
    void update(WeatherData data);
}
