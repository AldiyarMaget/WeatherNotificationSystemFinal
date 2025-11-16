package org.example.core;

public record WeatherData(
        String city,
        double temperature,
        double feelsLike,
        double minTemperature,
        double maxTemperature,
        double humidity,
        double cloudCover,
        double precipitationPercent,
        double windSpeed,
        String windDirection,
        String country,
        String sunrise,
        String sunset,
        String description,
        String date,
        String mainInfo
) {
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "Weather Data:\n" +
                "Temperature: " + temperature + "\n" +
                "Feels Like: " + feelsLike + "\n" +
                "Min Temperature: " + minTemperature + "\n" +
                "Max Temperature: " + maxTemperature + "\n" +
                "Humidity: " + humidity + "\n" +
                "Cloud Cover: " + cloudCover + "\n" +
                "Precipitation Probability: " + precipitationPercent + "\n" +
                "Wind Speed: " + windSpeed + "\n" +
                "Wind Direction: " + windDirection + "\n" +
                "Country: " + country + "\n" +
                "Sunrise: " + sunrise + "\n" +
                "Sunset: " + sunset + "\n" +
                "Description: " + description + "\n" +
                "Date: " + date + "\n" +
                "Main Info: " + mainInfo;
    }

    public static final class Builder {
        private String city;
        private double temperature = 0.0;
        private double feelsLike = 0.0;
        private double minTemperature = 0.0;
        private double maxTemperature = 0.0;
        private double humidity = 0.0;
        private int cloudCover = 0;
        private double precipitationPercent = 0.0;
        private double windSpeed = 0.0;
        private String windDirection = "";
        private String country = "";
        private String sunrise = "";
        private String sunset = "";
        private String description = "";
        private String date = "";
        private String main = "";

        public Builder() {
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder temperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder feelsLike(double feelsLike) {
            this.feelsLike = feelsLike;
            return this;
        }

        public Builder minTemperature(double minTemperature) {
            this.minTemperature = minTemperature;
            return this;
        }

        public Builder maxTemperature(double maxTemperature) {
            this.maxTemperature = maxTemperature;
            return this;
        }

        public Builder humidity(double humidity) {
            this.humidity = humidity;
            return this;
        }

        public Builder cloudCover(int cloudCover) {
            this.cloudCover = cloudCover;
            return this;
        }

        public Builder country(String country) {
            this.country = country != null ? country : "";
            return this;
        }

        public Builder sunrise(String sunrise) {
            this.sunrise = sunrise != null ? sunrise : "";
            return this;
        }

        public Builder sunset(String sunset) {
            this.sunset = sunset != null ? sunset : "";
            return this;
        }

        public Builder description(String description) {
            this.description = description != null ? description : "";
            return this;
        }

        public Builder date(String date) {
            this.date = date != null ? date : "";
            return this;
        }

        public Builder mainInfo(String main) {
            this.main = main != null ? main : "";
            return this;
        }

        public Builder precipitationPercent(double precipitationPercent) {
            this.precipitationPercent = precipitationPercent;
            return this;
        }

        public Builder windSpeed(double windSpeed) {
            this.windSpeed = windSpeed;
            return this;
        }

        public Builder windDirection(String windDirection) {
            this.windDirection = windDirection != null ? windDirection : "";
            return this;
        }

        public Builder cloudCover(double cloudCover) {
            this.cloudCover = (int) cloudCover;
            return this;
        }


        public WeatherData build() {
            return new WeatherData(
                    city,
                    temperature,
                    feelsLike,
                    minTemperature,
                    maxTemperature,
                    humidity,
                    cloudCover,
                    precipitationPercent,
                    windSpeed,
                    windDirection,
                    country,
                    sunrise,
                    sunset,
                    description,
                    date,
                    main
            );
        }


    }
}
