package org.example.notificationsStrategy;

public class ConcreteStrategyHour implements Strategy {

    @Override
    public long getIntervalMillis() {
        return 60 * 60 * 1000;
    }

    @Override
    public String getIntervalName() {
        return "Every Hour";
    }

    @Override
    public void getType() {

    }
}