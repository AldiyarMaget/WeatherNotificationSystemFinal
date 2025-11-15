package org.example.builderTelegram;
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
        .temp(openWeather.path("mainInfo").path("temp").asDouble(Double.NaN))
        .feelsLike(openWeather.path("mainInfo").path("feels_like").asDouble(Double.NaN))
        .humidity(openWeather.path("mainInfo").path("humidity").asInt(-1))
        .sunrise(parseTime(openWeather.path("sys").path("sunrise")))
        .sunset(parseTime(openWeather.path("sys").path("sunset")))
        .mainWeather(openWeather.path("weather").get(0).path("mainInfo").asText("-"))
        .description(openWeather.path("weather").get(0).path("description").asText("-"))
        .build();
*/