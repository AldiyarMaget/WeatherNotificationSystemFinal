package org.example.core;

import java.time.Instant;

public record WeatherData(double temperature, double humidity, double pressure, Instant timestamp) {

    @Override
    public String toString() {
        return String.format("WeatherData{temp=%.1f°C, hum=%.1f%%, pres=%.1f hPa, time=%s}",
                temperature, humidity, pressure, timestamp);
    }
}
