package org.example.notificationsStrategy;

public class ConcreteStrategyDaily implements Strategy {

    @Override
    public long getIntervalMillis() {
        return 24 * 60 * 60 * 1000;
    }

    @Override
    public String getIntervalName() {
        return "Every Day";
    }

    @Override
    public void getType() {

    }

}