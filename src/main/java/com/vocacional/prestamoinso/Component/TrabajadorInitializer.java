package com.vocacional.prestamoinso.Component;

import com.vocacional.prestamoinso.Entity.enums.ERole;
import com.vocacional.prestamoinso.Service.TrabajadorJpaService;
import com.vocacional.prestamoinso.Service.UserSupabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TrabajadorInitializer implements CommandLineRunner {

    @Autowired
    private TrabajadorJpaService trabajadorJpaService;

    @Autowired
    private UserSupabaseService userSupabaseService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("=== INICIALIZANDO TRABAJADOR ADMIN ===");
            
            // Verificar si JdbcTemplate está disponible
            if (jdbcTemplate == null) {
                System.out.println("JdbcTemplate no está disponible - saltando inicialización directa de trabajador");
                System.out.println("El usuario admin debe ser creado manualmente en Supabase");
                return;
            }
            
            // Verificar si ya existe un trabajador admin
            if (!trabajadorJpaService.findByUsername("admin").isPresent()) {
                // Verificar si existe en la tabla users
                var existingUser = userSupabaseService.findByUsername("admin");
                if (existingUser != null) {
                    // El usuario existe en users pero no en trabajador
                    // Insertar directamente en la tabla trabajador usando SQL nativo
                    System.out.println("Usuario admin encontrado en tabla users, creando registro en trabajador...");
                    
                    String sql = "INSERT INTO trabajador (id, needs_password_change) VALUES (?, ?) ON CONFLICT (id) DO NOTHING";
                    int rowsAffected = jdbcTemplate.update(sql, existingUser.getId(), false);
                    
                    if (rowsAffected > 0) {
                        System.out.println("Registro de trabajador creado exitosamente para usuario admin existente");
                    } else {
                        System.out.println("El registro de trabajador ya existe para el usuario admin");
                    }
                } else {
                    // Crear el usuario admin completo si no existe
                    System.out.println("Usuario admin no encontrado, creando usuario admin completo...");
                    
                    // Encriptar la contraseña
                    String encryptedPassword = passwordEncoder.encode("admin123");
                    
                    // Insertar en la tabla users con ON CONFLICT para evitar duplicados
                    String insertUserSql = "INSERT INTO users (nombre, apellido, username, email, password, role) VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (username) DO NOTHING";
                    int userRowsAffected = jdbcTemplate.update(insertUserSql, "Admin", "Sistema", "admin", "admin@sistema.com", encryptedPassword, "ADMIN");
                    
                    if (userRowsAffected > 0) {
                        // Obtener el ID del usuario recién creado
                        var newUser = userSupabaseService.findByUsername("admin");
                        if (newUser != null) {
                            // Insertar en la tabla trabajador
                            String insertTrabajadorSql = "INSERT INTO trabajador (id, needs_password_change) VALUES (?, ?) ON CONFLICT (id) DO NOTHING";
                            jdbcTemplate.update(insertTrabajadorSql, newUser.getId(), false);
                            
                            System.out.println("Usuario admin creado exitosamente con ID: " + newUser.getId());
                        }
                    } else {
                        System.out.println("El usuario admin ya existe en la base de datos");
                        // Intentar crear el registro de trabajador si no existe
                        var existingUserAfterConflict = userSupabaseService.findByUsername("admin");
                        if (existingUserAfterConflict != null) {
                            String insertTrabajadorSql = "INSERT INTO trabajador (id, needs_password_change) VALUES (?, ?) ON CONFLICT (id) DO NOTHING";
                            jdbcTemplate.update(insertTrabajadorSql, existingUserAfterConflict.getId(), false);
                            System.out.println("Verificado registro de trabajador para usuario admin existente");
                        }
                    }
                }
            } else {
                System.out.println("Usuario admin ya existe en la tabla trabajador");
            }
        } catch (Exception e) {
            System.err.println("Error al crear usuario admin: " + e.getMessage());
            e.printStackTrace();
        }
    }
}