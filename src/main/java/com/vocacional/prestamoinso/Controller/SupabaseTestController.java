package com.vocacional.prestamoinso.Controller;

import com.vocacional.prestamoinso.Service.SupabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/supabase")
public class SupabaseTestController {

    @Autowired
    private SupabaseService supabaseService;

    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println(">>> CONTROLADOR: Iniciando test connection <<<");
            
            if (supabaseService == null) {
                System.out.println(">>> ERROR: SupabaseService es null <<<");
                response.put("status", "error");
                response.put("message", "SupabaseService no está inyectado correctamente");
                response.put("connected", false);
                return ResponseEntity.status(500).body(response);
            }
            
            System.out.println(">>> CONTROLADOR: SupabaseService inyectado correctamente <<<");
            String result = supabaseService.testConnection();
            System.out.println(">>> CONTROLADOR: Resultado recibido: " + result + " <<<");
            
            if (result.contains("Conexión exitosa")) {
                response.put("status", "success");
                response.put("message", result);
                response.put("connected", true);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", result);
                response.put("connected", false);
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            System.out.println(">>> CONTROLADOR: Excepción capturada: " + e.getMessage() + " <<<");
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error al probar conexión: " + e.getMessage());
            response.put("connected", false);
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/test-select")
    public ResponseEntity<Map<String, Object>> testSelect() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String result = supabaseService.select("cliente", "*", "limit=5");
            
            response.put("status", "success");
            response.put("message", "Consulta SELECT exitosa");
            response.put("data", result);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error en consulta SELECT: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}