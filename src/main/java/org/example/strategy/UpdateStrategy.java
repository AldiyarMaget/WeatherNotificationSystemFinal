package org.example.strategy;

import org.example.core.WeatherStation;

public interface UpdateStrategy {
    void start(WeatherStation station);
    void stop();
    String name();
}
