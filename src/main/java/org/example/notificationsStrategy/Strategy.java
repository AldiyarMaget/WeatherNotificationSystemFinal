package org.example.notificationsStrategy;

public interface Strategy {
    long getIntervalMillis();
    String getIntervalName();

    void getType();
}