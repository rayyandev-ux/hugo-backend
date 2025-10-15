package com.vocacional.prestamoinso.Component;

import com.vocacional.prestamoinso.Entity.enums.ERole;
import com.vocacional.prestamoinso.Repository.TrabajadorRepository;
import com.vocacional.prestamoinso.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TrabajadorInitializer implements CommandLineRunner {

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Verificar si ya existe un trabajador admin
            if (!trabajadorRepository.findByUsername("admin").isPresent()) {
                // Verificar si existe en la tabla users
                var existingUser = userRepository.findByUsername("admin");
                if (existingUser != null) {
                    // El usuario existe en users pero no en trabajador
                    // Insertar directamente en la tabla trabajador usando SQL nativo
                    System.out.println("Usuario admin encontrado en tabla users, creando registro en trabajador...");
                    
                    String sql = "INSERT INTO trabajador (id, needs_password_change) VALUES (?, ?)";
                    jdbcTemplate.update(sql, existingUser.getId(), false);
                    
                    System.out.println("Registro de trabajador creado exitosamente para usuario admin existente");
                } else {
                    // Crear el usuario admin completo si no existe
                    System.out.println("Usuario admin no encontrado, creando usuario admin completo...");
                    
                    // Encriptar la contraseña
                    String encryptedPassword = passwordEncoder.encode("admin123");
                    
                    // Insertar en la tabla users
                    String insertUserSql = "INSERT INTO users (nombre, apellido, username, email, password, role) VALUES (?, ?, ?, ?, ?, ?)";
                    jdbcTemplate.update(insertUserSql, "Admin", "Sistema", "admin", "admin@sistema.com", encryptedPassword, "ADMIN");
                    
                    // Obtener el ID del usuario recién creado
                    var newUser = userRepository.findByUsername("admin");
                    if (newUser != null) {
                        // Insertar en la tabla trabajador
                        String insertTrabajadorSql = "INSERT INTO trabajador (id, needs_password_change) VALUES (?, ?)";
                        jdbcTemplate.update(insertTrabajadorSql, newUser.getId(), false);
                        
                        System.out.println("Usuario admin creado exitosamente con ID: " + newUser.getId());
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