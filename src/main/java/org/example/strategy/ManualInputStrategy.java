package org.example.strategy;

import org.example.core.WeatherData;
import org.example.core.WeatherStation;

public class ManualInputStrategy implements UpdateStrategy {
    private volatile WeatherStation station;

    @Override
    public void start(WeatherStation station) {
        this.station = station;
    }

    @Override
    public void stop() {
        this.station = null;
    }

    public void submit(WeatherData data) {
        WeatherStation st = station;
        if (st == null) throw new IllegalStateException("manual strategy not started");
        st.publish(data);
    }

    @Override
    public String name() {
        return "manual";
    }
}
