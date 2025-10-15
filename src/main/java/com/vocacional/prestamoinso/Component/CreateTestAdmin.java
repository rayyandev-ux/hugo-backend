package com.vocacional.prestamoinso.Component;

import com.vocacional.prestamoinso.Entity.User;
import com.vocacional.prestamoinso.Entity.enums.ERole;
import com.vocacional.prestamoinso.Service.UserSupabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10) // Ejecutar después de otros componentes
public class CreateTestAdmin implements CommandLineRunner {

    @Autowired
    private UserSupabaseService userSupabaseService;

    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("=== CREANDO USUARIO ADMINISTRADOR DE PRUEBA ===");
            
            // Verificar si ya existe el usuario testadmin
            User existingUser = userSupabaseService.findByUsername("testadmin");
            if (existingUser != null) {
                System.out.println("❌ El usuario 'testadmin' ya existe. Saltando creación.");
                return;
            }
            
            // Crear nuevo usuario administrador de prueba
            User testAdmin = new User();
            testAdmin.setNombre("Test");
            testAdmin.setApellido("Admin");
            testAdmin.setUsername("testadmin");
            testAdmin.setEmail("testadmin@sistema.com");
            testAdmin.setPassword("$2a$10$4ejBm0ekZKHanemiggJzNOU5cGUC77wZrReWF.wqUC45svNt41HW."); // test123
            testAdmin.setRole(ERole.ADMIN);
            
            // Guardar el usuario
            User savedUser = userSupabaseService.save(testAdmin);
            
            if (savedUser != null) {
                System.out.println("✅ Usuario administrador de prueba creado exitosamente:");
                System.out.println("   - ID: " + savedUser.getId());
                System.out.println("   - Username: " + savedUser.getUsername());
                System.out.println("   - Email: " + savedUser.getEmail());
                System.out.println("   - Role: " + savedUser.getRole());
                System.out.println("   - Contraseña: test123");
            } else {
                System.out.println("❌ Error al crear el usuario administrador de prueba");
            }
            
            System.out.println("=== CREACIÓN DE USUARIO DE PRUEBA COMPLETADA ===");
            
        } catch (Exception e) {
            System.err.println("❌ Error al crear usuario de prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
}