package org.example.app;

import java.util.LinkedHashMap;
import java.util.Map;

public final class LocationRegistry {

    public static class Loc {
        private final double latitude;
        private final double longitude;

        public Loc(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double latitude() { return latitude; }
        public double longitude() { return longitude; }
    }

    private static final Map<String, Loc> CITIES = new LinkedHashMap<>();

    static {
        CITIES.put("Astana", new Loc(51.1, 71.4));
        CITIES.put("Almaty", new Loc(43.2220, 76.8512));
        CITIES.put("Shymkent", new Loc(42.3417, 69.5901));
        CITIES.put("Karaganda", new Loc(49.8017, 73.1022));
    }

    private LocationRegistry() {}

    public static Map<String, Loc> listCities() {
        return new LinkedHashMap<>(CITIES);
    }

    public static Loc get(String name) {
        if (name == null) return null;
        String key = name.trim();
        Loc l = CITIES.get(key);
        if (l != null) return l;
        for (Map.Entry<String, Loc> e : CITIES.entrySet()) {
            if (e.getKey().equalsIgnoreCase(key)) return e.getValue();
        }
        return null;
    }
}
