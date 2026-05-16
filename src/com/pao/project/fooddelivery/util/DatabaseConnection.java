package com.pao.project.fooddelivery.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.sql.*;
import java.util.Properties;

public class DatabaseConnection {
    private Connection connection;

    private DatabaseConnection() {
        try {
            Properties prop = new Properties();
            try (FileInputStream fis = new FileInputStream("src/com/pao/project/fooddelivery/resources/db.properties")) {
                prop.load(fis);
            }

            String url = prop.getProperty("db.url");
            String driver = prop.getProperty("db.driver");
            Class.forName(driver); // incarca dinamic driver-ul
            connection = DriverManager.getConnection(url);

            try (Statement s = connection.createStatement()) {
                s.execute("PRAGMA foreign_keys = ON"); // ca sigur sa se valideze FK-uri in baza de date sqlite
            }

            // initializeaza schema daca nu exista
            if (!schemaExista()) {
                initializeazaSchema();
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

    public boolean schemaExista() {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='restaurante'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Eroare la verificarea existentei schemei bazei de date: ", e);
        }
    }

    private void initializeazaSchema() {
        String sql = "";
        // incercam o executie dinamica de sql
        // pentru initializarea schemei

        try (BufferedReader br = new BufferedReader(new FileReader("src/com/pao/project/fooddelivery/schema.sql"))) {
            String linie;
            while ((linie = br.readLine()) != null) { // un fel de while (fin >> linie) din c++
                sql += linie + "\n"; // in sql e practic acum un multiline string cu schema
            }

            String[] instructiuni = sql.split(";"); // se pot parsa instructiunile doar dupa ;

            // folosim Statement in loc de PreparedStatement pt ca nu modificam query-ul, doar executam ceva fix
            // putin cate putin
            try (Statement s = connection.createStatement()) {
                for (String instr : instructiuni) {
                    String crt = instr.trim();
                    if (!crt.isEmpty()) {
                        s.execute(crt);
                    }
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Eroare la initializarea schemei a bazei de date: ", e);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Eroare la initializarea schemei a bazei de date: ", e);
        }
    }
}
