package org.example.facade;

import org.example.RabbitMQ.Recv;
import org.example.core.WeatherStation;
import org.example.observer.ConsoleDisplay;
import org.example.sensor.*;
import org.example.strategy.StrategyFactory;
import org.example.strategy.UpdateStrategy;

import java.util.Scanner;

public class WeatherSystemFacade {

    private final Scanner scanner = new Scanner(System.in);
    private final StrategyFactory strategyFactory = new StrategyFactory();

    public void start() throws Exception {
        Recv.startListening();
        System.out.println("=== Weather Notification System (Facade) ===");

        while (true) {
            String city = askCity();
            Sensor sensor = chooseWeatherType();
            UpdateStrategy strategy = chooseStrategy(sensor, city);

            WeatherStation station = new WeatherStation();
            station.addObserver(new ConsoleDisplay("Console"));

            try {
                strategy.start(station);
            } catch (Exception ex) {
                System.out.println("Error starting strategy: " + ex.getMessage());
            }

            System.out.println("Strategy started. Press Enter to continue...");
            scanner.nextLine();
        }
    }

    private String askCity() {
        System.out.println("Enter city:");
        return scanner.nextLine().trim();
    }

    private Sensor chooseWeatherType() {
        System.out.println("Choose weather type:");
        System.out.println("1. Current weather");
        System.out.println("2. Hour weather");
        System.out.println("3. Today weather");
        System.out.println("4. Tomorrow weather");
        System.out.println("5. Forecast (5 days)");

        int opt = readInt();
        return switch (opt) {
            case 1 -> new GoogleWeatherCurrentSensor();
            case 2 -> new GoogleWeatherHourSensor();
            case 3 -> new GoogleWeatherTodaySensor();
            case 4 -> new GoogleWeatherTomorrowSensor();
            case 5 -> new GoogleWeatherWeeklySensor();
            default -> {
                System.out.println("Invalid choice. Default: current.");
                yield new GoogleWeatherCurrentSensor();
            }
        };
    }

    private UpdateStrategy chooseStrategy(Sensor sensor, String city) {
        System.out.println("Choose strategy:");
        System.out.println("1. Manual (one-time request)");
        System.out.println("2. Polling (repeat request)");

        int opt = readInt();

        return switch (opt) {
            case 1 -> strategyFactory.create(sensor, city);
            case 2 -> {
                System.out.println("Enter polling period (seconds, minimum 5):");
                int period = readInt();
                yield strategyFactory.create(sensor, period, city);
            }
            default -> {
                System.out.println("Invalid selection. Default: manual.");
                yield strategyFactory.create(sensor, city);
            }
        };
    }

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (Exception ignore) {
                System.out.println("Enter a valid number:");
            }
        }
    }

}
