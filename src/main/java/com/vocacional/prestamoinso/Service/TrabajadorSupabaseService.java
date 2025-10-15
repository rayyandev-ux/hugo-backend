package com.vocacional.prestamoinso.Service;

import com.vocacional.prestamoinso.Entity.Trabajador;
import com.vocacional.prestamoinso.Entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class TrabajadorSupabaseService {

    private static final Logger logger = LoggerFactory.getLogger(TrabajadorSupabaseService.class);

    @Autowired
    private SupabaseService supabaseService;

    private static final String TABLE_NAME = "trabajador";

    /**
     * Busca un trabajador por ID
     */
    public Optional<Trabajador> findById(Long id) {
        try {
            Trabajador trabajador = supabaseService.findById(TABLE_NAME, id, Trabajador.class);
            return Optional.ofNullable(trabajador);
        } catch (IOException e) {
            logger.error("Error al buscar trabajador por ID: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Busca un trabajador por nombre y apellido
     */
    public Optional<Trabajador> findByNombreAndApellido(String nombre, String apellido) {
        try {
            Trabajador trabajador = supabaseService.findByTwoFields(TABLE_NAME, "nombre", nombre, "apellido", apellido, Trabajador.class);
            return Optional.ofNullable(trabajador);
        } catch (IOException e) {
            logger.error("Error al buscar trabajador por nombre y apellido: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Busca un trabajador por username
     * Como Trabajador extiende User, buscamos en la tabla users y luego verificamos si es trabajador
     */
    public Optional<Trabajador> findByUsername(String username) {
        try {
            // Primero buscar el usuario en la tabla users
            User user = supabaseService.findByField("users", "username", username, User.class);
            if (user == null) {
                return Optional.empty();
            }
            
            // Luego verificar si existe en la tabla trabajador
            Trabajador trabajador = supabaseService.findById(TABLE_NAME, user.getId(), Trabajador.class);
            if (trabajador != null) {
                // Copiar los datos del usuario al trabajador
                trabajador.setId(user.getId());
                trabajador.setNombre(user.getNombre());
                trabajador.setApellido(user.getApellido());
                trabajador.setUsername(user.getUsername());
                trabajador.setEmail(user.getEmail());
                trabajador.setPassword(user.getPassword());
                trabajador.setRole(user.getRole());
                trabajador.setResetPasswordToken(user.getResetPasswordToken());
            }
            
            return Optional.ofNullable(trabajador);
        } catch (IOException e) {
            logger.error("Error al buscar trabajador por username: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Busca un trabajador por token de reset de contraseña
     * Como Trabajador extiende User, buscamos en la tabla users por el token
     */
    public Optional<Trabajador> findByResetPasswordToken(String token) {
        try {
            // Primero buscar el usuario en la tabla users por token
            User user = supabaseService.findByField("users", "reset_password_token", token, User.class);
            if (user == null) {
                return Optional.empty();
            }
            
            // Luego verificar si existe en la tabla trabajador
            Trabajador trabajador = supabaseService.findById(TABLE_NAME, user.getId(), Trabajador.class);
            if (trabajador != null) {
                // Copiar los datos del usuario al trabajador
                trabajador.setId(user.getId());
                trabajador.setNombre(user.getNombre());
                trabajador.setApellido(user.getApellido());
                trabajador.setUsername(user.getUsername());
                trabajador.setEmail(user.getEmail());
                trabajador.setPassword(user.getPassword());
                trabajador.setRole(user.getRole());
                trabajador.setResetPasswordToken(user.getResetPasswordToken());
            }
            
            return Optional.ofNullable(trabajador);
        } catch (IOException e) {
            logger.error("Error al buscar trabajador por token: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Busca todos los trabajadores
     */
    public List<Trabajador> findAll() {
        try {
            return supabaseService.findAll(TABLE_NAME, Trabajador.class);
        } catch (IOException e) {
            logger.error("Error al buscar todos los trabajadores: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Guarda un trabajador (insert o update)
     */
    public Trabajador save(Trabajador trabajador) {
        try {
            if (trabajador.getId() == null) {
                // Insert
                String result = supabaseService.insert(TABLE_NAME, trabajador);
                return supabaseService.parseJsonToObject(result, Trabajador.class);
            } else {
                // Update
                return supabaseService.updateById(TABLE_NAME, trabajador.getId(), trabajador, Trabajador.class);
            }
        } catch (IOException e) {
            logger.error("Error al guardar trabajador: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Elimina un trabajador por ID
     */
    public void deleteById(Long id) {
        try {
            supabaseService.deleteById(TABLE_NAME, id);
        } catch (IOException e) {
            logger.error("Error al eliminar trabajador: {}", e.getMessage(), e);
        }
    }

    /**
     * Elimina un trabajador
     */
    public void delete(Trabajador trabajador) {
        if (trabajador.getId() != null) {
            deleteById(trabajador.getId());
        }
    }

    /**
     * Cuenta el número total de trabajadores
     */
    public long count() {
        try {
            List<Trabajador> trabajadores = findAll();
            return trabajadores.size();
        } catch (Exception e) {
            logger.error("Error al contar trabajadores: {}", e.getMessage(), e);
            return 0L;
        }
    }
}