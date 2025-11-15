package org.example.builderTelegram;
//потом адаптирую
public class WeatherReportImpl implements WeatherReportBuilder {
    WeatherReport weatherReport = new WeatherReport();

    @Override
    public WeatherReportBuilder setCity(String city) {
        weatherReport.name = city;
        return this;
    }

    @Override
    public WeatherReportBuilder setCountry(String country) {
        weatherReport.country = country;
        return this;
    }

    @Override
    public WeatherReportBuilder setTemp(double temp) {
        weatherReport.temp = temp;
        return this;
    }

    @Override
    public WeatherReportBuilder setHum(int humidity) {
        weatherReport.humidity = humidity;
        return this;
    }

    @Override
    public WeatherReportBuilder setSunrise(String sunrise) {
        weatherReport.sunrise = sunrise;
        return this;
    }

    @Override
    public WeatherReportBuilder setSunset(String sunset) {
        weatherReport.sunset = sunset;
        return this;
    }

    @Override
    public WeatherReportBuilder serMainWeather(String mainWeather) {
        weatherReport.mainWeather = mainWeather;
        return this;
    }

    @Override
    public WeatherReportBuilder setDesc(String description) {
        weatherReport.description = description;
        return this;
    }


    @Override
    public WeatherReport build() {
        return weatherReport;
    }

}
