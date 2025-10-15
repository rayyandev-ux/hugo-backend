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
            
            // Verificar el estado actual de la contraseña del admin
            String checkPasswordSql = "SELECT id, username, CASE WHEN password IS NULL OR password = '' THEN 'VACÍA' ELSE 'CONFIGURADA' END as password_status FROM users WHERE username = 'admin'";
            
            jdbcTemplate.query(checkPasswordSql, (rs) -> {
                Long id = rs.getLong("id");
                String username = rs.getString("username");
                String passwordStatus = rs.getString("password_status");
                
                System.out.println("Usuario encontrado - ID: " + id + ", Username: " + username + ", Estado contraseña: " + passwordStatus);
                
                if ("VACÍA".equals(passwordStatus)) {
                    System.out.println("Contraseña vacía detectada. Corrigiendo...");
                    
                    // Actualizar la contraseña con el hash BCrypt correcto para 'admin123'
                    String updatePasswordSql = "UPDATE users SET password = ?, updated_at = NOW() WHERE username = 'admin'";
                    String bcryptPassword = "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd6dMlVBPbZpQ5I6";
                    
                    int rowsUpdated = jdbcTemplate.update(updatePasswordSql, bcryptPassword);
                    
                    if (rowsUpdated > 0) {
                        System.out.println("✅ Contraseña del usuario admin corregida exitosamente");
                        System.out.println("✅ Contraseña: admin123");
                    } else {
                        System.out.println("❌ Error: No se pudo actualizar la contraseña");
                    }
                } else {
                    System.out.println("✅ La contraseña del admin ya está configurada correctamente");
                }
            });
            
            System.out.println("=== CORRECCIÓN DE CONTRASEÑA ADMIN COMPLETADA ===");
            
        } catch (Exception e) {
            System.err.println("❌ Error al corregir contraseña del admin: " + e.getMessage());
            e.printStackTrace();
        }
    }
}