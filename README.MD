# Project Report / README (short version, English)

## Project Title

Weather Notification System

## Authors

Aldiar and Beibarys

---

## Overview

This project is a weather notification system demonstrating the usage of 6 design patterns: Adapter, Observer, Strategy, Builder, Facade, and Factory.
Weather data is requested through RabbitMQ and a Go service, stored in JSON, and then processed in Java into `WeatherData` before notifying observers.

---

## Where Each Pattern Is Used

### **1. Observer**

**Files:** `org.example.core.WeatherStation`, `org.example.observer.*`

* `WeatherStation` maintains observers and sends updates.
* `ConsoleDisplay` is the main observer, printing updated weather info.
* Used every time sensor data is updated.

### **2. Strategy**

**Files:** `org.example.strategy.UpdateStrategy`, `ManualInputStrategy`, `PollingInputStrategy`, `StrategyFactory`

* Defines how weather data is fetched: one-time (manual) or periodic (polling).
* `StrategyFactory` creates the appropriate strategy.
* The choice is made inside the Facade.

### **3. Factory**

**File:** `org.example.strategy.StrategyFactory`

* Creates different update strategies.
* Keeps main code clean by avoiding direct object creation.

### **4. Builder**

**File:** `org.example.core.WeatherData.Builder`

* Used by sensors to construct `WeatherData` objects.
* Simplifies filling multiple fields.

### **5. Adapter** (implemented implicitly through sensors)

**Files:** `org.example.sensor.*`

* Each `GoogleWeather*Sensor` converts external JSON into the internal `WeatherData` model.
* Sensors act as adapters by translating one data format into another.

### **6. Facade**

**File:** `org.example.facade.WeatherSystemFacade`

* Combines all system components: sensors, strategies, station, observers.
* Provides a simple interface — the user only chooses city, weather type, and strategy.
* `AppRunner` simply calls `facade.start()`.

Example:
`WeatherSystemFacade` creates a `WeatherStation`, adds `ConsoleDisplay`, selects a sensor and strategy, and starts the update process.

---

## Main Project Components

* `org.example.facade.WeatherSystemFacade` — the facade orchestrating the system.
* `org.example.core.WeatherStation` — observable subject.
* `org.example.observer.ConsoleDisplay` — observer receiving updates.
* `org.example.sensor.*` — sensors (adapters converting JSON → WeatherData).
* `org.example.strategy.*` — strategies & strategy factory.
* `org.example.RabbitMQ.*` — communication with Go service.
* `Go/API/*.go` — external weather API service.

---

## Summary

The system clearly demonstrates how six design patterns can work together in one project.
The Facade simplifies user interaction, Strategy controls request logic, Observer delivers notifications, and sensors use Adapter + Builder to generate the internal weather model.
