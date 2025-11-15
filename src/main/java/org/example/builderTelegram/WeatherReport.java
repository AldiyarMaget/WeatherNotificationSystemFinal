package org.example.builderTelegram;

public class WeatherReport {
    public String name;
    public String country;
    public double temp;
    public double feelsLike;
    public int humidity;
    public String sunrise;
    public String sunset;
    public String mainWeather;
    public String description;

    public void SendWeatherReport(){
        System.out.println(name);
        System.out.println(country);
        System.out.println(temp);
        System.out.println(feelsLike);
        System.out.println(humidity);
        System.out.println(sunrise);
        System.out.println(sunset);
        System.out.println(mainWeather);
        System.out.println(description);

    }

}
