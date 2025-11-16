package org.example.notificationsStrategy;

public class HourlyStrategy extends UpdateStrategy {

    @Override
    public long getIntervalMillis() { return 60L * 60 * 1000; }

    @Override
    public String name() { return "Hourly"; }
}

