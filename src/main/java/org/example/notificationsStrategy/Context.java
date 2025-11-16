package org.example.notificationsStrategy;

import org.example.core.WeatherStation;

public class Context {
    private UpdateStrategy strategy;

    public void setStrategy(UpdateStrategy strategy, WeatherStation station) {
        if (this.strategy != null) {
            this.strategy.stop();
        }

        this.strategy = strategy;
        strategy.start(station);
    }

    public String getCurrentStrategyName() {
        return strategy != null ? strategy.name() : "No strategy set";
    }
}
