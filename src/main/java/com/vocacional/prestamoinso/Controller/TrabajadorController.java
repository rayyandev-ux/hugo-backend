package com.vocacional.prestamoinso.Controller;


import com.vocacional.prestamoinso.DTO.TrabajadorDTO;
import com.vocacional.prestamoinso.DTO.LoginRequestDTO;
import com.vocacional.prestamoinso.Entity.Trabajador;
import com.vocacional.prestamoinso.Entity.User;
import com.vocacional.prestamoinso.Service.TrabajadorJpaService;
import com.vocacional.prestamoinso.Service.UserJpaService;
import com.vocacional.prestamoinso.Service.JwtUtilService;
import com.vocacional.prestamoinso.Service.TrabajadorService;
import com.vocacional.prestamoinso.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/trabajador")
public class TrabajadorController {

    @Autowired
    private TrabajadorService trabajadorService;

    @Autowired
    private JwtUtilService jwtUtilService;
    @Autowired
    private TrabajadorJpaService trabajadorJpaService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserJpaService userJpaService;
    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password,
            @RequestBody(required = false) LoginRequestDTO body
    ) {
        Map<String, Object> response = new HashMap<>();

        // Extraer credenciales del cuerpo JSON si está presente
        if (body != null) {
            username = body.getUsername();
            password = body.getPassword();
        }

        if (username == null || password == null) {
            response.put("message", "Faltan credenciales: username y password");
            return ResponseEntity.badRequest().body(response);
        }

        // Usar findByUsername para obtener el usuario
        Optional<User> userOpt = userJpaService.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Debug: verificar el password obtenido
            System.out.println("DEBUG LOGIN - Usuario encontrado: " + user.getUsername());
            System.out.println("DEBUG LOGIN - Password obtenido: " + (user.getPassword() != null ? "SÍ (length: " + user.getPassword().length() + ")" : "NO"));
            System.out.println("DEBUG LOGIN - Password primeros 20 chars: " + (user.getPassword() != null && user.getPassword().length() > 20 ? user.getPassword().substring(0, 20) + "..." : user.getPassword()));
            
            // Verificar si es un trabajador
            Optional<Trabajador> optionalTrabajador = trabajadorJpaService.findByUsername(username);
            if (optionalTrabajador.isPresent()) {
                Trabajador trabajador = optionalTrabajador.get();
                
                // Debug: verificar el password antes de la comparación
                System.out.println("DEBUG LOGIN - Password para comparar: " + (user.getPassword() != null ? "PRESENTE" : "NULL"));
                System.out.println("DEBUG LOGIN - Password ingresado: " + password);
                
                // Usar el password del user que tiene el password completo
                if (user.getPassword() == null || user.getPassword().isEmpty()) {
                    System.out.println("DEBUG LOGIN - Password es null o vacío, rechazando login");
                    response.put("message", "Error interno: password no disponible");
                    return ResponseEntity.badRequest().body(response);
                }
                
                if (!passwordEncoder.matches(password, user.getPassword())) {
                    System.out.println("DEBUG LOGIN - Password no coincide");
                    response.put("message", "Contraseña incorrecta");
                    return ResponseEntity.badRequest().body(response);
                }

                System.out.println("DEBUG LOGIN - Password coincide, generando token");
                String token = jwtUtilService.generateToken(trabajador);

                response.put("message", "Inicio de sesión exitoso");
                response.put("token", token);
                response.put("requiresPasswordChange", trabajador.isNeedsPasswordChange());
                return ResponseEntity.ok(response);
            }
        }

        response.put("message", "Trabajador no registrado");
        return ResponseEntity.badRequest().body(response);
    }


    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerWorker(@Validated @RequestBody TrabajadorDTO trabajadorDTO) {
        try {
            userService.registerTrabajador(trabajadorDTO);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuario registrado con éxito.");
            return ResponseEntity.ok(response);
        } catch (DataIntegrityViolationException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "El email ya está registrado.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error interno del servidor.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/register-admin")
    public ResponseEntity<Map<String, String>> registerAdmin(@Validated @RequestBody TrabajadorDTO trabajadorDTO) {
        try {
            userService.registerAdmin(trabajadorDTO);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuario registrado con éxito.");
            return ResponseEntity.ok(response);
        } catch (DataIntegrityViolationException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "El email ya está registrado.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error interno del servidor.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            trabajadorService.deleteUser(id);
            return ResponseEntity.ok("Usuario eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar el usuario: " + e.getMessage());
        }
    }


    @GetMapping("/me")
    public ResponseEntity<User> getUserInfo(@RequestHeader("Authorization") String token) {

        String jwt = token.substring(7);

        String username = jwtUtilService.extractUsername(jwt);

        Optional<User> usuarioOpt = userJpaService.findByUsername(username);

        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(usuarioOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String newPassword,
            @RequestBody(required = false) Map<String, String> requestBody) {
        
        // Obtener parámetros de JSON o request params
        String usernameValue = username;
        String passwordValue = newPassword;
        
        // Si vienen en el body JSON, los usamos
        if (requestBody != null) {
            usernameValue = requestBody.getOrDefault("username", usernameValue);
            passwordValue = requestBody.getOrDefault("newPassword", passwordValue);
        }
        
        // Validar que tengamos los datos necesarios
        if (usernameValue == null || passwordValue == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Se requiere username y newPassword");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        Map<String, Object> response = new HashMap<>();

        Optional<Trabajador> optionalTrabajador = trabajadorJpaService.findByUsername(usernameValue);
        if (optionalTrabajador.isPresent()) {
            Trabajador trabajador = optionalTrabajador.get();

            trabajador.setPassword(passwordEncoder.encode(passwordValue));
            trabajador.setNeedsPasswordChange(false);
            trabajadorJpaService.save(trabajador);

            response.put("message", "Contraseña cambiada exitosamente");
            return ResponseEntity.ok(response);
        }

        response.put("message", "Trabajador no encontrado");
        return ResponseEntity.badRequest().body(response);
    }



    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestParam(required = false) String email,
            @RequestBody(required = false) Map<String, String> requestBody) {
        
        // Obtener email de JSON o request params
        String emailValue = email;
        
        // Si viene en el body JSON, lo usamos
        if (requestBody != null && requestBody.containsKey("email")) {
            emailValue = requestBody.get("email");
        }
        
        // Validar que tengamos el email
        if (emailValue == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body(Map.of("error", "Se requiere el email"));
        }
        
        try {
            trabajadorService.generateResetPasswordToken(emailValue);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body(Map.of("message", "Se ha enviado un correo con instrucciones para restablecer su contraseña."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body(Map.of("error", "Error al generar el token de recuperación: " + e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String newPassword,
            @RequestBody(required = false) Map<String, String> requestBody) {
        
        // Obtener parámetros de JSON o request params
        String tokenValue = token;
        String passwordValue = newPassword;
        
        // Si vienen en el body JSON, los usamos
        if (requestBody != null) {
            tokenValue = requestBody.getOrDefault("token", tokenValue);
            passwordValue = requestBody.getOrDefault("newPassword", passwordValue);
        }
        
        // Validar que tengamos los datos necesarios
        if (tokenValue == null || passwordValue == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Se requiere token y newPassword"));
        }
        
        try {
            trabajadorService.resetPassword(tokenValue, passwordValue);

            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada exitosamente."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Error al restablecer la contraseña: " + e.getMessage()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<java.util.List<Trabajador>> listarTrabajadores() {
        try {
            java.util.List<Trabajador> trabajadores = trabajadorService.listarTodos();
            return ResponseEntity.ok(trabajadores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
