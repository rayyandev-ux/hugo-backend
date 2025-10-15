package com.vocacional.prestamoinso.Component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Order(4) // Ejecutar después del SupabaseDebugger
public class DirectDatabaseChecker implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("=== VERIFICACIÓN DIRECTA DE BASE DE DATOS ===");
            
            // 1. Verificar si la tabla users existe
            System.out.println("1. Verificando existencia de tabla users:");
            try {
                String tableExistsQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'users'";
                Integer tableCount = jdbcTemplate.queryForObject(tableExistsQuery, Integer.class);
                System.out.println("Tabla users existe: " + (tableCount > 0));
            } catch (Exception e) {
                System.out.println("Error verificando tabla: " + e.getMessage());
            }
            
            // 2. Verificar estructura de la tabla users
            System.out.println("\n2. Estructura de la tabla users:");
            try {
                String columnsQuery = "SELECT column_name, data_type, is_nullable FROM information_schema.columns WHERE table_name = 'users' ORDER BY ordinal_position";
                List<Map<String, Object>> columns = jdbcTemplate.queryForList(columnsQuery);
                for (Map<String, Object> column : columns) {
                    System.out.println("Columna: " + column.get("column_name") + 
                                     " | Tipo: " + column.get("data_type") + 
                                     " | Nullable: " + column.get("is_nullable"));
                }
            } catch (Exception e) {
                System.out.println("Error obteniendo estructura: " + e.getMessage());
            }
            
            // 3. Verificar datos del usuario admin
            System.out.println("\n3. Datos del usuario admin:");
            try {
                String adminQuery = "SELECT id, username, password, LENGTH(password) as password_length FROM users WHERE username = 'admin'";
                List<Map<String, Object>> adminData = jdbcTemplate.queryForList(adminQuery);
                if (!adminData.isEmpty()) {
                    Map<String, Object> admin = adminData.get(0);
                    System.out.println("ID: " + admin.get("id"));
                    System.out.println("Username: " + admin.get("username"));
                    System.out.println("Password: " + admin.get("password"));
                    System.out.println("Password Length: " + admin.get("password_length"));
                    System.out.println("Password is null: " + (admin.get("password") == null));
                } else {
                    System.out.println("Usuario admin no encontrado");
                }
            } catch (Exception e) {
                System.out.println("Error obteniendo datos del admin: " + e.getMessage());
            }
            
            // 4. Verificar todos los usuarios
            System.out.println("\n4. Todos los usuarios en la tabla:");
            try {
                String allUsersQuery = "SELECT id, username, password IS NOT NULL as has_password FROM users";
                List<Map<String, Object>> allUsers = jdbcTemplate.queryForList(allUsersQuery);
                for (Map<String, Object> user : allUsers) {
                    System.out.println("ID: " + user.get("id") + 
                                     " | Username: " + user.get("username") + 
                                     " | Has Password: " + user.get("has_password"));
                }
            } catch (Exception e) {
                System.out.println("Error obteniendo todos los usuarios: " + e.getMessage());
            }
            
            System.out.println("=== VERIFICACIÓN DIRECTA COMPLETADA ===");
            
        } catch (Exception e) {
            System.err.println("Error en verificación directa: " + e.getMessage());
            e.printStackTrace();
        }
    }
}