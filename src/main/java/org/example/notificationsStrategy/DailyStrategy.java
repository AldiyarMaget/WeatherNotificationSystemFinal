package org.example.notificationsStrategy;

public class DailyStrategy extends UpdateStrategy {

    @Override
    public long getIntervalMillis() { return 24L * 60 * 60 * 1000; }

    @Override
    public String name() { return "Daily"; }
}

