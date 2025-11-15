package org.example.notificationsStrategy;

public class ConcreteStrategyWeekly implements Strategy{
    @Override
    public long getIntervalMillis() {
        return 60 * 60 * 1000*7;
    }

    @Override
    public String getIntervalName() {
        return "Every Hour";
    }

    @Override
    public void getType() {

    }
}
