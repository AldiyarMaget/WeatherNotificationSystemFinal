package org.example.app;

import org.example.observer.ConsoleDisplay;
import org.example.observer.Observer;
import org.example.observer.PhoneAppObserver;
import org.example.strategy.ManualInputStrategy;
import org.example.strategy.StrategyFactory;
import org.example.strategy.UpdateStrategy;
import org.example.core.WeatherStation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class WeatherAppFacade {
    private final WeatherStation station = new WeatherStation();
    private final Map<String, Observer> observers = Collections.synchronizedMap(new LinkedHashMap<>());

    public String registerConsole(String displayName) {
        String name = safeName(displayName, "console");
        synchronized (observers) {
            if (observers.containsKey(name)) return "observer '" + name + "' already exists";
            Observer o = new ConsoleDisplay(name);
            observers.put(name, o);
            station.addSubscriber(o);
            return "console attached: " + name;
        }
    }

    public String registerPhone(String deviceId) {
        String id = safeName(deviceId, "phone1");
        synchronized (observers) {
            if (observers.containsKey(id)) return "observer '" + id + "' already exists";
            Observer o = new PhoneAppObserver(id);
            observers.put(id, o);
            station.addSubscriber(o);
            return "phone attached: " + id;
        }
    }

    public String unregisterObserver(String name) {
        if (name == null || name.trim().isEmpty()) return "name required";
        synchronized (observers) {
            Observer o = observers.remove(name);
            if (o == null) return "no observer '" + name + "'";
            station.removeSubscriber(o);
            return "detached " + name;
        }
    }

    public String enableManualInput() {
        UpdateStrategy st = StrategyFactory.createManual();
        station.setStrategy(st);
        return "manual strategy set";
    }

    public String startSimulatedPolling(long periodSeconds) {
        try {
            Map<String, String> params = new LinkedHashMap<>();
            params.put("type", "sim");
            params.put("period", Long.toString(Math.max(1, periodSeconds)));
            UpdateStrategy st = StrategyFactory.createPolling(params);
            station.setStrategy(st);
            return "polling(sim) strategy set: " + st.name();
        } catch (Exception e) {
            return "failed to set polling(sim): " + e.getMessage();
        }
    }

    public String startYandexPolling(String apiKey, double lat, double lon, long periodSeconds) {
        try {
            Map<String, String> params = new LinkedHashMap<>();
            params.put("type", "yandex");
            params.put("apikey", apiKey == null ? "" : apiKey);
            params.put("lat", Double.toString(lat));
            params.put("lon", Double.toString(lon));
            params.put("period", Long.toString(Math.max(1, periodSeconds)));
            UpdateStrategy st = StrategyFactory.createPolling(params);
            station.setStrategy(st);
            return "polling(yandex) strategy set: " + st.name();
        } catch (Exception e) {
            return "failed to set polling(yandex): " + e.getMessage();
        }
    }

    public String submitManualReading(double temperature, double humidity, double pressure) {
        UpdateStrategy cur = station.getCurrentStrategy();
        if (cur instanceof ManualInputStrategy) {
            try {
                ((ManualInputStrategy) cur).submit(
                        new org.example.core.WeatherData(temperature, humidity, pressure, java.time.Instant.now())
                );
                return "manual data submitted";
            } catch (Exception e) {
                return "failed to submit manual data: " + e.getMessage();
            }
        }
        return "current strategy is not manual";
    }

    public String listObservers() {
        synchronized (observers) {
            if (observers.isEmpty()) return "<no observers>";
            StringBuilder sb = new StringBuilder();
            for (String k : observers.keySet()) {
                sb.append(k).append("\n");
            }
            return sb.toString().trim();
        }
    }

    public String getStatus() {
        UpdateStrategy cur = station.getCurrentStrategy();
        String sName = cur == null ? "<none>" : cur.name();
        int count;
        synchronized (observers) { count = observers.size(); }
        return "strategy=" + sName + ", observers=" + count;
    }

    public String shutdown() {
        station.shutdown();
        return "shutting down";
    }

    private String safeName(String name, String def) {
        if (name == null) return def;
        name = name.trim();
        return name.isEmpty() ? def : name;
    }
}
