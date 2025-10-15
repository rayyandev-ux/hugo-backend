package com.vocacional.prestamoinso.Component;

import com.vocacional.prestamoinso.Entity.User;
import com.vocacional.prestamoinso.Service.SupabaseService;
import com.vocacional.prestamoinso.Service.UserSupabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3) // Ejecutar después del AdminPasswordFixer
public class SupabaseDebugger implements CommandLineRunner {

    @Autowired
    private SupabaseService supabaseService;
    
    @Autowired
    private UserSupabaseService userSupabaseService;

    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("=== INICIANDO DEBUG DE SUPABASE ===");
            
            // 1. Verificar qué devuelve la consulta directa a Supabase
            System.out.println("1. Consulta directa a Supabase:");
            String rawResult = supabaseService.select("users", "*", "username=eq.admin");
            System.out.println("Raw JSON response: " + rawResult);
            
            // 1.1 Verificar específicamente el campo password
            System.out.println("\n1.1 Consulta específica del campo password:");
            String passwordResult = supabaseService.select("users", "id,username,password", "username=eq.admin");
            System.out.println("Password field JSON response: " + passwordResult);
            
            // 2. Verificar qué devuelve findByField
            System.out.println("\n2. Resultado de findByField:");
            User user = supabaseService.findByField("users", "username", "admin", User.class);
            if (user != null) {
                System.out.println("User ID: " + user.getId());
                System.out.println("User Username: " + user.getUsername());
                System.out.println("User Password: " + user.getPassword());
                System.out.println("User Password length: " + (user.getPassword() != null ? user.getPassword().length() : "NULL"));
                System.out.println("User Password is null: " + (user.getPassword() == null));
                System.out.println("User Password is empty: " + (user.getPassword() != null && user.getPassword().isEmpty()));
            } else {
                System.out.println("User is NULL");
            }
            
            // 3. Verificar qué devuelve UserSupabaseService
            System.out.println("\n3. Resultado de UserSupabaseService.findByUsername:");
            User userFromService = userSupabaseService.findByUsername("admin");
            if (userFromService != null) {
                System.out.println("Service User ID: " + userFromService.getId());
                System.out.println("Service User Username: " + userFromService.getUsername());
                System.out.println("Service User Password: " + userFromService.getPassword());
                System.out.println("Service User Password length: " + (userFromService.getPassword() != null ? userFromService.getPassword().length() : "NULL"));
                System.out.println("Service User Password is null: " + (userFromService.getPassword() == null));
                System.out.println("Service User Password is empty: " + (userFromService.getPassword() != null && userFromService.getPassword().isEmpty()));
            } else {
                System.out.println("Service User is NULL");
            }
            
            System.out.println("=== DEBUG DE SUPABASE COMPLETADO ===");
            
        } catch (Exception e) {
            System.err.println("Error en debug de Supabase: " + e.getMessage());
            e.printStackTrace();
        }
    }
}