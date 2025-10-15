package com.vocacional.prestamoinso.Component;

import com.vocacional.prestamoinso.Entity.User;
import com.vocacional.prestamoinso.Service.UserSupabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(2) // Ejecutar después del TrabajadorInitializer
public class AdminPasswordFixer implements CommandLineRunner {

    @Autowired
    private UserSupabaseService userSupabaseService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("=== INICIANDO CORRECCIÓN DE CONTRASEÑA ADMIN ===");
            
            // Buscar el usuario admin usando el servicio de Supabase
            User adminUser = userSupabaseService.findByUsername("admin");
            
            if (adminUser != null) {
                System.out.println("=== INFORMACIÓN DETALLADA DEL USUARIO ADMIN ===");
                System.out.println("ID: " + adminUser.getId());
                System.out.println("Username: " + adminUser.getUsername());
                System.out.println("Password length: " + (adminUser.getPassword() != null ? adminUser.getPassword().length() : 0));
                System.out.println("Password status: " + (adminUser.getPassword() == null ? "NULL" : adminUser.getPassword().isEmpty() ? "VACÍA" : "CONFIGURADA"));
                System.out.println("Password (primeros 20 chars): " + (adminUser.getPassword() != null && adminUser.getPassword().length() > 20 ? adminUser.getPassword().substring(0, 20) + "..." : adminUser.getPassword()));
                
                // SIEMPRE actualizar la contraseña para asegurar que esté correcta
                System.out.println("⚠ Forzando actualización de contraseña...");
                
                String newPassword = "admin123";
                String hashedPassword = passwordEncoder.encode(newPassword);
                
                // Actualizar la contraseña usando el servicio de Supabase
                adminUser.setPassword(hashedPassword);
                userSupabaseService.save(adminUser);
                
                System.out.println("✅ Contraseña del usuario admin actualizada exitosamente");
                System.out.println("✅ Nueva contraseña: " + newPassword);
                System.out.println("✅ Hash BCrypt: " + hashedPassword);
                System.out.println("✅ Verificación - Nueva longitud: " + hashedPassword.length());
                System.out.println("✅ Verificación - Hash correcto: " + (hashedPassword.startsWith("$2a$") ? "SÍ" : "NO"));
                
            } else {
                System.out.println("❌ Usuario admin no encontrado");
            }
            
            System.out.println("=== CORRECCIÓN DE CONTRASEÑA ADMIN COMPLETADA ===");
            
        } catch (Exception e) {
            System.err.println("❌ Error al corregir contraseña del admin: " + e.getMessage());
            e.printStackTrace();
        }
    }
}