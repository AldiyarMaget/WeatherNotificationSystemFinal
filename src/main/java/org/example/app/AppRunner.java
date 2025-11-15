package org.example.app;

import org.example.sensor.GoogleWeatherCurrentSensor;
import org.example.sensor.Sensor;
import org.example.strategy.StrategyFactory;

import java.util.Scanner;

public class AppRunner {
    private final static String WELCOME_MESSAGE = "Welcome to the Weather Notification System!";
    private final static String CHOOSE_OPTION_MESSAGE = "Chose option: ";
    private static final String LIST_OPTION_MESSAGE = "\"1. Current Weather\"+\"\\n\"+\"2. Weather forecast\"+\"\\n\"+\"3. Change city\"+\"\\n\"";
    private static final String CHOOSE_WEATHER_APP = "Choose Weather App: ";
    private static final String LIST_WEATHER_APP = "1. Google Weather"+"\n"+"2. Open Weather"+"\n"+"3. Weather API"+"\n";
    private static String city;

    StrategyFactory strategyFactory = new StrategyFactory();
    Scanner scanner = new Scanner(System.in);

    public void run(){
        System.out.println(WELCOME_MESSAGE);

        while(true){
            System.out.println("Which city do you want to know the weather in?");
            this.city = scanner.nextLine();

            System.out.println(LIST_OPTION_MESSAGE);
            System.out.println(CHOOSE_OPTION_MESSAGE);
            int option = scanner.nextInt();

            switch(option){
                case 1:
                    chooseWeatherApp();
            }
        }

    }

    private void chooseWeatherApp(){
        Sensor sensor;
        System.out.println(CHOOSE_WEATHER_APP);
        System.out.println(LIST_WEATHER_APP);

        int option = scanner.nextInt();

    }

    private void chooseStrategy(Sensor sensor){
        System.out.println("Which strategy do you want to use?");
        System.out.println("1. Manual"+"\n"+"2. Polling"+"\n");

        int option = scanner.nextInt();
        switch(option){
            case 1:
                strategyFactory.create(sensor);
                break;
            case 2:
                choosePeriod(sensor);
        }
    }

    private void choosePeriod(Sensor sensor){

    }
}
