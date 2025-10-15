package com.vocacional.prestamoinso.Service;

import com.vocacional.prestamoinso.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UserSupabaseService {

    @Autowired
    private SupabaseService supabaseService;

    private static final String TABLE_NAME = "users";

    /**
     * Busca un usuario por ID
     */
    public Optional<User> findById(Long id) {
        try {
            User user = supabaseService.findById(TABLE_NAME, id, User.class);
            return Optional.ofNullable(user);
        } catch (IOException e) {
            System.err.println("Error al buscar usuario por ID: " + e.getMessage());
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
            System.err.println("Error al buscar usuario por username: " + e.getMessage());
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
            System.err.println("Error al buscar usuario por email: " + e.getMessage());
            return null;
        }
    }

    /**
     * Busca un usuario por token de reset de contraseña
     */
    public User findByResetPasswordToken(String token) {
        try {
            return supabaseService.findByField(TABLE_NAME, "resetPasswordToken", token, User.class);
        } catch (IOException e) {
            System.err.println("Error al buscar usuario por token: " + e.getMessage());
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
            System.err.println("Error al buscar todos los usuarios: " + e.getMessage());
            return List.of();
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
            System.err.println("Error al guardar usuario: " + e.getMessage());
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
            System.err.println("Error al eliminar usuario: " + e.getMessage());
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
            System.err.println("Error al contar usuarios: " + e.getMessage());
            return 0;
        }
    }
}