package com.pao.project.fooddelivery.util;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection {
    private Connection connection;

    private DatabaseConnection() {
        try {
            Properties prop = new Properties();
            try (FileInputStream fis = new FileInputStream("src/com/pao/project/fooddelivery/db.properties")) {
                prop.load(fis);
            }

            String url = prop.getProperty("db.url");
            String driver = prop.getProperty("db.driver");
            Class.forName(driver);
            connection = DriverManager.getConnection(url);

            try (Statement s = connection.createStatement()) {
                s.execute("PRAGMA foreign_keys = ON"); // ca sigur sa se valideze FK-uri in baza de date sqlite
            }
        } catch (Exception e) {
            throw new RuntimeException("Eroare la conectarea la baza de date: ", e);
        }
    }

    private static class Holder {
        private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    }

    public static DatabaseConnection getInstance() {
        return Holder.INSTANCE;
    }

    public Connection getConnection() {
        return connection;
    }
}
