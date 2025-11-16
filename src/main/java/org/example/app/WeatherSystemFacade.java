package org.example.app;

import org.example.RabbitMQ.Recv;
import org.example.core.WeatherStation;
import org.example.observer.ConsoleDisplay;
import org.example.sensor.Sensor;
import org.example.strategy.StrategyFactory;
import org.example.strategy.UpdateStrategy;

public class WeatherSystemFacade {

    private final StrategyFactory strategyFactory;
    private final ConsoleUI consoleUI;

    public WeatherSystemFacade(StrategyFactory strategyFactory, ConsoleUI consoleUI) {
        this.strategyFactory = strategyFactory;
        this.consoleUI = consoleUI;
    }

    public static WeatherSystemFacade createDefault() {
        StrategyFactory strategyFactory = new StrategyFactory();
        ConsoleUI consoleUI = new ConsoleUI(strategyFactory);
        return new WeatherSystemFacade(strategyFactory, consoleUI);
    }

    public void start() throws Exception {
        Recv.startListening();
        System.out.println("=== Weather Notification System (Facade) ===");

        while (true) {
            String city = consoleUI.askCity();
            Sensor sensor = consoleUI.chooseWeatherType();
            UpdateStrategy strategy = consoleUI.chooseStrategy(sensor, city);

            WeatherStation station = new WeatherStation();
            station.addObserver(new ConsoleDisplay("Console"));

            try {
                strategy.start(station);
            } catch (Exception ex) {
                System.out.println("Error starting strategy: " + ex.getMessage());
            }

            consoleUI.waitForContinue();
        }
    }
}
