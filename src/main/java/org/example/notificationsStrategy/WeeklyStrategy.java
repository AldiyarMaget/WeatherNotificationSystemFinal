package org.example.notificationsStrategy;

import org.example.core.WeatherStation;
import org.example.core.WeatherData;

public class WeeklyStrategy extends UpdateStrategy {
    @Override
    public long getIntervalMillis() { return 7L * 24 * 60 * 60 * 1000; }

    @Override
    public String name() { return "Weekly"; }
}

