package org.example.notificationsStrategy;

import org.example.core.WeatherStation;
import org.example.core.WeatherData;

public class HourlyStrategy extends UpdateStrategy {
    @Override
    public long getIntervalMillis() { return 60L*60*1000; }
    @Override
    public String name() { return "Hourly"; }
}
