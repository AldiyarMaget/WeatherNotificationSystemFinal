package org.example.observer;


import org.example.core.WeatherData;

public interface Observed {
    void addSubscriber(Observer subscriber);
    void removeSubscriber(Observer subscriber);
    void publish(WeatherData data);

}


