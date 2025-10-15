package com.vocacional.prestamoinso.Component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(2) // Ejecutar después del TrabajadorInitializer
public class AdminPasswordFixer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("=== INICIANDO CORRECCIÓN DE CONTRASEÑA ADMIN ===");
            
            // Verificar el estado actual de la contraseña del admin con más detalle
            String checkPasswordSql = "SELECT id, username, password, LENGTH(password) as password_length, CASE WHEN password IS NULL THEN 'NULL' WHEN password = '' THEN 'VACÍA' ELSE 'CONFIGURADA' END as password_status FROM users WHERE username = 'admin'";
            
            jdbcTemplate.query(checkPasswordSql, (rs) -> {
                Long id = rs.getLong("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                int passwordLength = rs.getInt("password_length");
                String passwordStatus = rs.getString("password_status");
                
                System.out.println("=== INFORMACIÓN DETALLADA DEL USUARIO ADMIN ===");
                System.out.println("ID: " + id);
                System.out.println("Username: " + username);
                System.out.println("Password length: " + passwordLength);
                System.out.println("Password status: " + passwordStatus);
                System.out.println("Password (primeros 20 chars): " + (password != null && password.length() > 20 ? password.substring(0, 20) + "..." : password));
                
                // SIEMPRE actualizar la contraseña para asegurar que esté correcta
                System.out.println("🔧 Forzando actualización de contraseña...");
                
                String updatePasswordSql = "UPDATE users SET password = ?, updated_at = NOW() WHERE username = 'admin'";
                String bcryptPassword = "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd6dMlVBPbZpQ5I6";
                
                int rowsUpdated = jdbcTemplate.update(updatePasswordSql, bcryptPassword);
                
                if (rowsUpdated > 0) {
                    System.out.println("✅ Contraseña del usuario admin actualizada exitosamente");
                    System.out.println("✅ Nueva contraseña: admin123");
                    System.out.println("✅ Hash BCrypt: " + bcryptPassword);
                    
                    // Verificar la actualización
                    String verifyPasswordSql = "SELECT password, LENGTH(password) as new_length FROM users WHERE username = 'admin'";
                    jdbcTemplate.query(verifyPasswordSql, (rs2) -> {
                        String newPassword = rs2.getString("password");
                        int newLength = rs2.getInt("new_length");
                        System.out.println("🔍 Verificación - Nueva longitud: " + newLength);
                        System.out.println("🔍 Verificación - Hash correcto: " + (bcryptPassword.equals(newPassword) ? "SÍ" : "NO"));
                    });
                } else {
                    System.out.println("❌ Error: No se pudo actualizar la contraseña");
                }
            });
            
            System.out.println("=== CORRECCIÓN DE CONTRASEÑA ADMIN COMPLETADA ===");
            
        } catch (Exception e) {
            System.err.println("❌ Error al corregir contraseña del admin: " + e.getMessage());
            e.printStackTrace();
        }
    }
}