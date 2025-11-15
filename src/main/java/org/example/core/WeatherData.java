package org.example.core;

public record WeatherData(
        String city,
        double temperature,
        double feelsLike,
        double minTemperature,
        double maxTemperature,
        double humidity,
        String country,
        String sunrise,
        String sunset,
        String description,
        String mainInfo
) {
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "city='" + city + '\'' +
                ", temperature=" + temperature +
                ", feelsLike=" + feelsLike +
                ", minTemperature=" + minTemperature +
                ", maxTemperature=" + maxTemperature +
                ", humidity=" + humidity +
                ", country='" + country + '\'' +
                ", sunrise='" + sunrise + '\'' +
                ", sunset='" + sunset + '\'' +
                ", description='" + description + '\'' +
                ", mainInfo='" + mainInfo + '\'' +
                '}';
    }

    public static final class Builder {
        private String city;
        private double temperature = 0.0;
        private double feelsLike = 0.0;
        private double minTemperature = 0.0;
        private double maxTemperature = 0.0;
        private double humidity = 0.0;
        private String country = "";
        private String sunrise = "";
        private String sunset = "";
        private String description = "";
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

        public Builder mainInfo(String main) {
            this.main = main != null ? main : "";
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
                    country,
                    sunrise,
                    sunset,
                    description,
                    main
            );
        }
    }
}
