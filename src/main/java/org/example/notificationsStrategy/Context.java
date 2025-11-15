package org.example.notificationsStrategy;

public class Context {
    private Strategy strategy;

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public void getWeather(){
        strategy.getIntervalName();
    }

}