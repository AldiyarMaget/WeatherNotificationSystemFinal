package org.example.observer;

import org.example.core.WeatherData;

public interface Observer {
    void update(WeatherData data);
}
