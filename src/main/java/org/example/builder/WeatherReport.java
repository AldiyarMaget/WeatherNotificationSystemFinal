package org.example.builder;
//потом адаптирую
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

/*WeatherReport report = new WeatherReport.Builder()
        .city(openWeather.path("name").asText("-"))
        .country(openWeather.path("sys").path("country").asText("-"))
        .temp(openWeather.path("main").path("temp").asDouble(Double.NaN))
        .feelsLike(openWeather.path("main").path("feels_like").asDouble(Double.NaN))
        .humidity(openWeather.path("main").path("humidity").asInt(-1))
        .sunrise(parseTime(openWeather.path("sys").path("sunrise")))
        .sunset(parseTime(openWeather.path("sys").path("sunset")))
        .mainWeather(openWeather.path("weather").get(0).path("main").asText("-"))
        .description(openWeather.path("weather").get(0).path("description").asText("-"))
        .build();
*/