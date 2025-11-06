package org.example.observer;

import org.example.core.WeatherData;

public class ConsoleDisplay implements Observer {
    private final String name;
    public ConsoleDisplay(String name) { this.name = name == null ? "console" : name; }

    @Override
    public void update(WeatherData data) {
        System.out.println("[" + name + "] update: " + data);
    }

    @Override
    public String toString() { return "ConsoleDisplay(" + name + ")"; }
}
