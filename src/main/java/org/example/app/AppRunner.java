package org.example.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AppRunner {
    private final WeatherAppFacade app;
    private final Scanner scanner = new Scanner(System.in);

    public AppRunner(WeatherAppFacade app) {
        this.app = app;
    }

    public void run() {
        System.out.println("Weather Console App — type 'help' for commands");
        while (true) {
            System.out.print("> ");
            String line = nextLine();
            if (line == null) break;
            if (line.isEmpty()) continue;
            String[] tokens = line.split("\\s+");
            String cmd = tokens[0].toLowerCase();
            if ("select".equals(cmd) && tokens.length >= 2 && "strategy".equalsIgnoreCase(tokens[1])) {
                interactiveSelectStrategy();
                continue;
            }
            switch (cmd) {
                case "help":
                    printHelp();
                    break;
                case "attach":
                    cmdAttach(tokens);
                    break;
                case "detach":
                    cmdDetach(tokens);
                    break;
                case "set-strategy":
                    cmdSetStrategy(tokens);
                    break;
                case "submit":
                    cmdSubmit(tokens);
                    break;
                case "list":
                    System.out.println(app.listObservers());
                    break;
                case "status":
                    System.out.println(app.getStatus());
                    break;
                case "exit":
                    System.out.println(app.shutdown());
                    return;
                default:
                    System.out.println("unknown command: " + cmd);
            }
        }
    }

    private void interactiveSelectStrategy() {
        System.out.println("Select a strategy:");
        System.out.println("  1) manual");
        System.out.println("  2) polling - sim (simulated sensor)");
        System.out.println("  3) polling - yandex (real API)");
        String choice = readLine("Enter number or name (default 1): ", "1").trim().toLowerCase();
        if ("1".equals(choice) || "manual".equals(choice)) {
            System.out.println(app.enableManualInput());
            return;
        }
        if ("2".equals(choice) || "sim".equals(choice) || "polling-sim".equals(choice)) {
            long period = askPeriod(30L);
            System.out.println(app.startSimulatedPolling(period));
            return;
        }
        if ("3".equals(choice) || "yandex".equals(choice) || "polling-yandex".equals(choice)) {
            String apiKey = readLine("Enter Yandex API key (leave empty to use env YANDEX_API_KEY): ", "").trim();
            if (apiKey.isEmpty()) apiKey = null;
            Map<String, LocationRegistry.Loc> cities = LocationRegistry.listCities();
            List<Map.Entry<String, LocationRegistry.Loc>> list = new ArrayList<>(cities.entrySet());
            System.out.println("Select name:");
            for (int i = 0; i < list.size(); i++) {
                Map.Entry<String, LocationRegistry.Loc> e = list.get(i);
                System.out.printf("  %d) %s (lat=%s, lon=%s)%n", i + 1, e.getKey(), e.getValue().latitude(), e.getValue().longitude());
            }
            String cityChoice = readLine("Enter number or name name (default 1): ", "1").trim();
            double lat = 51.1;
            double lon = 71.4;
            try {
                int num = Integer.parseInt(cityChoice);
                if (num >= 1 && num <= list.size()) {
                    Map.Entry<String, LocationRegistry.Loc> sel = list.get(num - 1);
                    lat = sel.getValue().latitude();
                    lon = sel.getValue().longitude();
                }
            } catch (NumberFormatException ex) {
                LocationRegistry.Loc loc = LocationRegistry.get(cityChoice);
                if (loc != null) { lat = loc.latitude(); lon = loc.longitude(); }
            }
            long period = askPeriod(30L);
            System.out.println(app.startYandexPolling(apiKey, lat, lon, period));
            return;
        }
        System.out.println("unknown selection, aborted.");
    }

    private void cmdAttach(String[] tokens) {
        if (tokens.length < 3) {
            System.out.println("usage: attach console|phone <name>");
            return;
        }
        String type = tokens[1].toLowerCase();
        String name = tokens[2];
        if ("console".equals(type)) {
            System.out.println(app.registerConsole(name));
        } else if ("phone".equals(type)) {
            System.out.println(app.registerPhone(name));
        } else {
            System.out.println("unknown attach type");
        }
    }

    private void cmdDetach(String[] tokens) {
        if (tokens.length < 2) {
            System.out.println("usage: detach <name>");
            return;
        }
        System.out.println(app.unregisterObserver(tokens[1]));
    }

    private void cmdSetStrategy(String[] tokens) {
        if (tokens.length < 2) {
            System.out.println("usage: set-strategy manual|polling ... (or use 'select strategy' for interactive)");
            return;
        }
        String type = tokens[1].toLowerCase();
        if ("manual".equals(type)) {
            System.out.println(app.enableManualInput());
            return;
        }
        if ("polling".equals(type)) {
            if (tokens.length >= 3 && "sim".equalsIgnoreCase(tokens[2])) {
                long period = tokens.length >= 4 ? parsePeriodToken(tokens[3]) : 30;
                System.out.println(app.startSimulatedPolling(period));
                return;
            }
            if (tokens.length >= 3 && "yandex".equalsIgnoreCase(tokens[2])) {
                String apiKey = null;
                double lat = 51.1;
                double lon = 71.4;
                long period = 30;
                for (int i = 3; i < tokens.length; i++) {
                    String t = tokens[i];
                    if (t.startsWith("apiKey=") || t.startsWith("apikey=")) apiKey = t.substring(t.indexOf('=') + 1);
                    else if (t.startsWith("lat=")) lat = parseDoubleOrDefault(t.substring(t.indexOf('=') + 1), lat);
                    else if (t.startsWith("lon=")) lon = parseDoubleOrDefault(t.substring(t.indexOf('=') + 1), lon);
                    else if (t.startsWith("period=")) period = parseLongOrDefault(t.substring(t.indexOf('=') + 1), period);
                }
                System.out.println(app.startYandexPolling(apiKey, lat, lon, period));
                return;
            }
            System.out.println("usage: set-strategy polling sim|yandex ...");
            return;
        }
        System.out.println("unknown strategy");
    }

    private void cmdSubmit(String[] tokens) {
        if (tokens.length < 4) {
            System.out.println("usage: submit <temp> <hum> <pres>");
            return;
        }
        try {
            double t = Double.parseDouble(tokens[1]);
            double h = Double.parseDouble(tokens[2]);
            double p = Double.parseDouble(tokens[3]);
            System.out.println(app.submitManualReading(t, h, p));
        } catch (NumberFormatException e) {
            System.out.println("invalid numbers");
        }
    }

    private String nextLine() {
        try {
            return scanner.nextLine().trim();
        } catch (Exception e) {
            return null;
        }
    }

    private String readLine(String prompt, String defaultValue) {
        System.out.print(prompt);
        String line = nextLine();
        if (line == null) return defaultValue;
        if (line.isEmpty()) return defaultValue;
        return line.trim();
    }

    private long askPeriod(long defaultSeconds) {
        String p = readLine("Enter polling period in seconds (min 30) [" + defaultSeconds + "]: ", Long.toString(defaultSeconds));
        return parsePeriodToken(p);
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  attach console <name>");
        System.out.println("  attach phone <id>");
        System.out.println("  detach <name>");
        System.out.println("  set-strategy manual");
        System.out.println("  set-strategy polling sim [period=N]");
        System.out.println("  set-strategy polling yandex apiKey=<KEY> [lat=..] [lon=..] [period=..]");
        System.out.println("  select strategy      # interactive strategy selector");
        System.out.println("  submit <temp> <hum> <pres>");
        System.out.println("  list");
        System.out.println("  status");
        System.out.println("  exit");
    }

    private long parsePeriodToken(String token) {
        try {
            if (token.startsWith("period=")) token = token.substring(token.indexOf('=') + 1);
            return Long.parseLong(token);
        } catch (Exception e) {
            return 30L;
        }
    }

    private long parseLongOrDefault(String s, long def) {
        try { return Long.parseLong(s); } catch (Exception e) { return def; }
    }

    private double parseDoubleOrDefault(String s, double def) {
        try { return Double.parseDouble(s); } catch (Exception e) { return def; }
    }
}
