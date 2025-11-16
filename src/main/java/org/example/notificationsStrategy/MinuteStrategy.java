package org.example.notificationsStrategy;

public class MinuteStrategy extends UpdateStrategy {

    @Override
    public long getIntervalMillis() {
        return 60 * 1000;
    }

    @Override
    public String name() {
        return "Minute";
    }
}
