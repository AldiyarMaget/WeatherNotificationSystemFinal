package org.example.strategy;

import org.example.sensor.SimulatedSensor;
import org.example.sensor.YandexWeatherSensor;

import java.util.Map;

public class StrategyFactory {
    private static final String DEFAULT_LAT = "51.1";
    private static final String DEFAULT_LON = "71.4";
    private static final String DEFAULT_TYPE = "sim";
    private static final String DEFAULT_PERIOD = "30";

    public static UpdateStrategy createPolling(Map<String, String> params) {
        String stype = params.getOrDefault("type", DEFAULT_TYPE).toLowerCase();
        long period;
        try {
            period = Long.parseLong(params.getOrDefault("period", DEFAULT_PERIOD));
            if (period <= 0) period = Long.parseLong(DEFAULT_PERIOD);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid period: must be integer seconds");
        }
        if ("yandex".equals(stype)) {
            String key = params.get("apikey");
            if (key == null || key.isBlank()) key = System.getenv("YANDEX_API_KEY");
            if (key == null || key.isBlank()) throw new IllegalArgumentException("missing apiKey for yandex");
            double lat;
            double lon;
            try {
                lat = Double.parseDouble(params.getOrDefault("lat", DEFAULT_LAT));
                lon = Double.parseDouble(params.getOrDefault("lon", DEFAULT_LON));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("invalid lat/lon");
            }
            return new SensorPollingStrategy(new YandexWeatherSensor(key, lat, lon), period);
        } else {
            return new SensorPollingStrategy(new SimulatedSensor(), period);
        }
    }

    public static UpdateStrategy createManual() {
        return new ManualInputStrategy();
    }
}
