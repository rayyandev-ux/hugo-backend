package com.vocacional.prestamoinso.Component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
@Order(1)
public class DbConnectivityProbe implements CommandLineRunner {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== DB Connectivity Probe ===");
        System.out.println("URL: " + url);
        System.out.println("User: " + username);
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connection OK. AutoCommit: " + conn.getAutoCommit());
        } catch (SQLException ex) {
            System.out.println("Connection FAILED: " + ex.getMessage());
            Throwable cause = ex.getCause();
            if (cause != null) {
                System.out.println("Cause: " + cause.getMessage());
            }
        }
    }
}