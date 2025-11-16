package org.example.subscribers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionDB {
    private final String url = "jdbc:postgresql://localhost:5432/postgres";
    private final String user = "postgres";
    private final String password = "123456";

    public SubscriptionDB() {
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS subscriptions (
                    id SERIAL PRIMARY KEY,
                    chat_id BIGINT NOT NULL,
                    city TEXT NOT NULL,
                    strategy_type TEXT NOT NULL
                )
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addSubscription(long chatId, String city, String strategyType) {
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO subscriptions(chat_id, city, strategy_type) VALUES(?,?,?)")) {
            ps.setLong(1, chatId);
            ps.setString(2, city);
            ps.setString(3, strategyType);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeSubscription(int id) {
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM subscriptions WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Subscription> getSubscriptions(long chatId) {
        List<Subscription> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM subscriptions WHERE chat_id=?")) {
            ps.setLong(1, chatId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Subscription(
                        rs.getInt("id"),
                        rs.getLong("chat_id"), //айдишка пользователя
                        rs.getString("city"),
                        rs.getString("strategy_type") // здесь хранится интервал
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public List<Subscription> getAllSubscriptions() {
        List<Subscription> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM subscriptions")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Subscription(
                        rs.getInt("id"),
                        rs.getLong("chat_id"),  //айдишка пользователя
                        rs.getString("city"),
                        rs.getString("strategy_type") // здесь хранится интервал
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }



    public static class Subscription {
        public final int id;
        public final long chatId;
        public final String city;
        public final String strategyType;

        public Subscription(int id, long chatId, String city, String strategyType) {
            this.id = id;
            this.chatId = chatId;
            this.city = city;
            this.strategyType = strategyType;
        }
    }


}
