package org.example.strategy;

import org.example.sensor.*;

import java.util.Objects;

public class StrategyFactory {


    public UpdateStrategy create(Sensor sensor, int period, String city) {
        Objects.requireNonNull(sensor, "sensor required");
        Objects.requireNonNull(period, "period required");
        if (period <=0) {
            throw new IllegalArgumentException("period указан не верно");
        }
        return new PollingInputStrategy(sensor, period, city);
    }

    public UpdateStrategy create(Sensor sensor, String city) {
        Objects.requireNonNull(sensor, "sensor required");
        return new ManualInputStrategy(sensor,city);
    }
}
