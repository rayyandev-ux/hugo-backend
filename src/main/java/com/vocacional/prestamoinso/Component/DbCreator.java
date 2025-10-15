package com.vocacional.prestamoinso.Component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Component
@Profile("dev")
@Order(0)
public class DbCreator implements CommandLineRunner {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Override
    public void run(String... args) throws Exception {
        try {
            String jdbc = datasourceUrl;
            // Parse host, port y nombre de base desde la URL JDBC (MySQL)
            // Formato: jdbc:mysql://host:port/db?params
            String uriStr = jdbc.replace("jdbc:", "");
            URI uri = URI.create(uriStr);
            String host = uri.getHost();
            int port = (uri.getPort() == -1) ? 3306 : uri.getPort();
            String path = uri.getPath();
            String dbName = (path != null && path.length() > 1) ? path.substring(1) : "mysql";

            String adminUrl = String.format("jdbc:mysql://%s:%d/?useSSL=false&serverTimezone=UTC", host, port);

            try (Connection conn = DriverManager.getConnection(adminUrl, username, password)) {
                boolean exists;
                try (PreparedStatement ps = conn.prepareStatement("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?")) {
                    ps.setString(1, dbName);
                    exists = ps.executeQuery().next();
                }

                if (!exists) {
                    System.out.println("=== DbCreator: creando base de datos '" + dbName + "' ===");
                    try (Statement st = conn.createStatement()) {
                        st.execute("CREATE DATABASE `" + dbName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                    }
                    System.out.println("=== DbCreator: base de datos creada ===");
                } else {
                    System.out.println("=== DbCreator: base de datos ya existe ===");
                }
            }
        } catch (SQLException ex) {
            System.out.println("=== DbCreator: error creando base de datos: " + ex.getMessage());
        } catch (Exception e) {
            System.out.println("=== DbCreator: error inesperado: " + e.getMessage());
        }
    }
}