package org.example.builder;
//потом адаптирую
public interface WeatherReportBuilder {
    WeatherReportBuilder setCity(String city);
    WeatherReportBuilder setCountry(String country);
    WeatherReportBuilder setTemp(double temp);
    WeatherReportBuilder setHum(int humidity);
    WeatherReportBuilder setSunrise(String sunrise);
    WeatherReportBuilder setSunset(String sunset);
    WeatherReportBuilder serMainWeather(String mainWeather);
    WeatherReportBuilder setDesc(String description);

    WeatherReport build();
}
