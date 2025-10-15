package com.vocacional.prestamoinso.Service;

import com.vocacional.prestamoinso.Entity.User;
import com.vocacional.prestamoinso.Entity.enums.ERole;
import com.vocacional.prestamoinso.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserJpaService {

    private static final Logger logger = LoggerFactory.getLogger(UserJpaService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findById(Long id) {
        try {
            return userRepository.findById(id);
        } catch (Exception e) {
            logger.error("Error al buscar usuario por ID: {}", id, e);
            return Optional.empty();
        }
    }

    public Optional<User> findByUsername(String username) {
        try {
            User user = userRepository.findByUsername(username);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error al buscar usuario por username: {}", username, e);
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {
        try {
            User user = userRepository.findByEmail(email);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error al buscar usuario por email: {}", email, e);
            return Optional.empty();
        }
    }

    public User save(User user) {
        try {
            // Encriptar contraseña si no está encriptada
            if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            
            return userRepository.save(user);
        } catch (Exception e) {
            logger.error("Error al guardar usuario: {}", user.getUsername(), e);
            throw new RuntimeException("Error al guardar usuario", e);
        }
    }

    public List<User> findAll() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            logger.error("Error al obtener todos los usuarios", e);
            throw new RuntimeException("Error al obtener usuarios", e);
        }
    }

    public void deleteById(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error al eliminar usuario con ID: {}", id, e);
            throw new RuntimeException("Error al eliminar usuario", e);
        }
    }

    public boolean existsByUsername(String username) {
        try {
            return userRepository.findByUsername(username) != null;
        } catch (Exception e) {
            logger.error("Error al verificar existencia de username: {}", username, e);
            return false;
        }
    }

    public boolean existsByEmail(String email) {
        try {
            return userRepository.findByEmail(email) != null;
        } catch (Exception e) {
            logger.error("Error al verificar existencia de email: {}", email, e);
            return false;
        }
    }

    public User createAdminUser(String nombre, String apellido, String username, String email, String password) {
        try {
            User admin = new User();
            admin.setNombre(nombre);
            admin.setApellido(apellido);
            admin.setUsername(username);
            admin.setEmail(email);
            admin.setPassword(password); // Se encriptará en save()
            admin.setRole(ERole.ADMIN);
            
            return save(admin);
        } catch (Exception e) {
            logger.error("Error al crear usuario administrador", e);
            throw new RuntimeException("Error al crear usuario administrador", e);
        }
    }

    public Optional<User> findByResetPasswordToken(String token) {
        try {
            User user = userRepository.findByResetPasswordToken(token);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error al buscar usuario por token de reset: {}", token, e);
            return Optional.empty();
        }
    }

    public void updatePassword(Long userId, String newPassword) {
        try {
            Optional<User> userOpt = findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setPassword(newPassword); // Se encriptará en save()
                save(user);
            } else {
                throw new RuntimeException("Usuario no encontrado");
            }
        } catch (Exception e) {
            logger.error("Error al actualizar contraseña del usuario: {}", userId, e);
            throw new RuntimeException("Error al actualizar contraseña", e);
        }
    }
}