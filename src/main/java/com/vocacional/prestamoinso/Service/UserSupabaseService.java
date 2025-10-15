package com.vocacional.prestamoinso.Service;

import com.vocacional.prestamoinso.Entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserSupabaseService {

    private static final Logger logger = LoggerFactory.getLogger(UserSupabaseService.class);

    @Autowired
    private SupabaseService supabaseService;
    
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    private static final String TABLE_NAME = "users";

    /**
     * Busca un usuario por ID
     */
    public Optional<User> findById(Long id) {
        try {
            User user = supabaseService.findById(TABLE_NAME, id, User.class);
            return Optional.ofNullable(user);
        } catch (IOException e) {
            logger.error("Error al buscar usuario por ID: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Busca un usuario por username
     */
    public User findByUsername(String username) {
        try {
            return supabaseService.findByField(TABLE_NAME, "username", username, User.class);
        } catch (IOException e) {
            logger.error("Error al buscar usuario por username: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Busca un usuario por email
     */
    public User findByEmail(String email) {
        try {
            return supabaseService.findByField(TABLE_NAME, "email", email, User.class);
        } catch (IOException e) {
            logger.error("Error al buscar usuario por email: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Busca un usuario por username usando JdbcTemplate como fallback
     * Este método se usa cuando Supabase API filtra el campo password por RLS
     */
    public User findByUsernameWithPassword(String username) {
        try {
            // Primero intentar con Supabase API
            User user = supabaseService.findByField(TABLE_NAME, "username", username, User.class);
            
            // Si el usuario existe pero no tiene password, intentar obtenerlo con consulta específica
            if (user != null && user.getPassword() == null) {
                logger.info("Password filtrado por RLS, intentando consulta específica de password");
                
                try {
                    // Intentar obtener solo el campo password con una consulta específica
                    String passwordResult = supabaseService.select(TABLE_NAME, "password", "username=eq." + username);
                    if (passwordResult != null && !passwordResult.trim().equals("[]")) {
                        // Parsear el resultado JSON para obtener el password
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        com.fasterxml.jackson.databind.JsonNode jsonArray = mapper.readTree(passwordResult);
                        if (jsonArray.isArray() && jsonArray.size() > 0) {
                            com.fasterxml.jackson.databind.JsonNode firstResult = jsonArray.get(0);
                            if (firstResult.has("password") && !firstResult.get("password").isNull()) {
                                user.setPassword(firstResult.get("password").asText());
                                logger.info("Password obtenido exitosamente desde consulta específica de Supabase");
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.warn("No se pudo obtener password con consulta específica: {}", e.getMessage());
                }
                
                // Si aún no tenemos password y JdbcTemplate está disponible, usarlo como último recurso
                if (user.getPassword() == null && jdbcTemplate != null) {
                    try {
                        logger.info("Usando JdbcTemplate como último recurso para obtener password");
                        String query = "SELECT id, nombre, apellido, username, email, password, role, reset_password_token, created_at, updated_at FROM users WHERE username = ?";
                        List<Map<String, Object>> results = jdbcTemplate.queryForList(query, username);
                        
                        if (!results.isEmpty()) {
                            Map<String, Object> row = results.get(0);
                            user.setPassword((String) row.get("password"));
                            logger.info("Password obtenido exitosamente desde base de datos directa");
                        }
                    } catch (Exception e) {
                        logger.warn("JdbcTemplate no disponible o falló: {}", e.getMessage());
                    }
                }
            }
            
            return user;
        } catch (Exception e) {
            logger.error("Error al buscar usuario por username con password: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Busca todos los usuarios
     */
    public List<User> findAll() {
        try {
            return supabaseService.findAll(TABLE_NAME, User.class);
        } catch (IOException e) {
            logger.error("Error al buscar todos los usuarios: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Busca un usuario por token de reset de contraseña
     */
    public User findByResetPasswordToken(String token) {
        try {
            return supabaseService.findByField(TABLE_NAME, "reset_password_token", token, User.class);
        } catch (IOException e) {
            logger.error("Error al buscar usuario por token: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Guarda un usuario (insert o update)
     */
    public User save(User user) {
        try {
            if (user.getId() == null) {
                // Insert
                String result = supabaseService.insert(TABLE_NAME, user);
                return supabaseService.parseJsonToObject(result, User.class);
            } else {
                // Update
                return supabaseService.updateById(TABLE_NAME, user.getId(), user, User.class);
            }
        } catch (IOException e) {
            logger.error("Error al guardar usuario: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Elimina un usuario por ID
     */
    public void deleteById(Long id) {
        try {
            supabaseService.deleteById(TABLE_NAME, id);
        } catch (IOException e) {
            logger.error("Error al eliminar usuario: {}", e.getMessage(), e);
        }
    }

    /**
     * Elimina un usuario
     */
    public void delete(User user) {
        if (user.getId() != null) {
            deleteById(user.getId());
        }
    }

    /**
     * Cuenta el número total de usuarios
     */
    public long count() {
        try {
            List<User> users = findAll();
            return users.size();
        } catch (Exception e) {
            logger.error("Error al contar usuarios: {}", e.getMessage(), e);
            return 0L;
        }
    }
}