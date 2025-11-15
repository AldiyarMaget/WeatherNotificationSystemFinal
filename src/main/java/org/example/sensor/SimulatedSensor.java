package org.example.sensor;
/*
import org.example.core.WeatherData;
import org.example.core.exceptions.SensorException;

import java.util.List;
import java.util.Random;

//TODO пусть будет для теста

public class SimulatedSensor implements Sensor {
    private final Random rnd = new Random();

    @Override
    public List<WeatherData> read() throws SensorException {
        String city = "TestCity";

        double temperature = 5 + rnd.nextDouble() * 25;
        double feelsLike = 4 + rnd.nextDouble() * 25;
        double minTemperature = 5 + rnd.nextDouble() * 10;
        double maxTemperature = 5 + rnd.nextDouble() * 35;
        double humidity = 20 + rnd.nextDouble() * 60;
        String country = "TestCountry";
        String sunrise = "06:00AM";
        String sunset = "07:00PM";
        String mainInfo = "TestMainInfo";
        String description = "TestDescription";
        return WeatherData
                .builder()
                .city(city)
                .temperature(temperature)
                .feelsLike(feelsLike)
                .minTemperature(minTemperature)
                .maxTemperature(maxTemperature)
                .humidity(humidity)
                .sunrise(sunrise)
                .sunset(sunset)
                .country(country)
                .description(description)
                .mainInfo(mainInfo)
                .build();
    }
}
*/