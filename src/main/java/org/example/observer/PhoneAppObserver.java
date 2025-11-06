package org.example.observer;

import org.example.core.WeatherData;

public class PhoneAppObserver implements Observer {
    private final String deviceId;
    public PhoneAppObserver(String deviceId) { this.deviceId = deviceId; }

    @Override
    public void update(WeatherData data) {
        System.out.println("[Phone " + deviceId + "] push: " + data);
    }

    @Override
    public String toString() { return "PhoneApp(" + deviceId + ")"; }
}
