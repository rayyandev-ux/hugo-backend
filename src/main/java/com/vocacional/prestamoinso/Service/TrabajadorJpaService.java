package com.vocacional.prestamoinso.Service;

import com.vocacional.prestamoinso.Entity.Trabajador;
import com.vocacional.prestamoinso.Entity.User;
import com.vocacional.prestamoinso.Repository.TrabajadorRepository;
import com.vocacional.prestamoinso.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrabajadorJpaService {

    private static final Logger logger = LoggerFactory.getLogger(TrabajadorJpaService.class);

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Busca un trabajador por ID
     */
    public Optional<Trabajador> findById(Long id) {
        try {
            return trabajadorRepository.findById(id);
        } catch (Exception e) {
            logger.error("Error al buscar trabajador por ID: {}", id, e);
            return Optional.empty();
        }
    }

    /**
     * Busca un trabajador por nombre y apellido
     */
    public Optional<Trabajador> findByNombreAndApellido(String nombre, String apellido) {
        try {
            return trabajadorRepository.findByNombreAndApellido(nombre, apellido);
        } catch (Exception e) {
            logger.error("Error al buscar trabajador por nombre y apellido: {} {}", nombre, apellido, e);
            return Optional.empty();
        }
    }

    /**
     * Busca un trabajador por username
     * Como Trabajador extiende User, buscamos directamente en la tabla trabajador
     */
    public Optional<Trabajador> findByUsername(String username) {
        try {
            return trabajadorRepository.findByUsername(username);
        } catch (Exception e) {
            logger.error("Error al buscar trabajador por username: {}", username, e);
            return Optional.empty();
        }
    }

    /**
     * Busca un trabajador por token de reset de contraseña
     * Como Trabajador extiende User, buscamos directamente en la tabla trabajador
     */
    public Optional<Trabajador> findByResetPasswordToken(String token) {
        try {
            return trabajadorRepository.findByResetPasswordToken(token);
        } catch (Exception e) {
            logger.error("Error al buscar trabajador por token: {}", token, e);
            return Optional.empty();
        }
    }

    /**
     * Busca todos los trabajadores
     */
    public List<Trabajador> findAll() {
        try {
            return trabajadorRepository.findAll();
        } catch (Exception e) {
            logger.error("Error al buscar todos los trabajadores", e);
            throw new RuntimeException("Error al obtener trabajadores", e);
        }
    }

    /**
     * Guarda un trabajador (insert o update)
     */
    public Trabajador save(Trabajador trabajador) {
        try {
            return trabajadorRepository.save(trabajador);
        } catch (Exception e) {
            logger.error("Error al guardar trabajador: {}", trabajador, e);
            throw new RuntimeException("Error al guardar trabajador", e);
        }
    }

    /**
     * Elimina un trabajador por ID
     */
    public void deleteById(Long id) {
        try {
            trabajadorRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error al eliminar trabajador con ID: {}", id, e);
            throw new RuntimeException("Error al eliminar trabajador", e);
        }
    }

    /**
     * Elimina un trabajador
     */
    public void delete(Trabajador trabajador) {
        try {
            if (trabajador.getId() != null) {
                trabajadorRepository.deleteById(trabajador.getId());
            }
        } catch (Exception e) {
            logger.error("Error al eliminar trabajador: {}", trabajador, e);
            throw new RuntimeException("Error al eliminar trabajador", e);
        }
    }

    /**
     * Cuenta el número total de trabajadores
     */
    public long count() {
        try {
            return trabajadorRepository.count();
        } catch (Exception e) {
            logger.error("Error al contar trabajadores", e);
            return 0L;
        }
    }
}