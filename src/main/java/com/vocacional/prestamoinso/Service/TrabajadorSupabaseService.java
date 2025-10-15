package com.vocacional.prestamoinso.Service;

import com.vocacional.prestamoinso.Entity.Trabajador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class TrabajadorSupabaseService {

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
            System.err.println("Error al buscar trabajador por ID: " + e.getMessage());
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
            System.err.println("Error al buscar trabajador por nombre y apellido: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Busca un trabajador por username
     */
    public Optional<Trabajador> findByUsername(String username) {
        try {
            Trabajador trabajador = supabaseService.findByField(TABLE_NAME, "username", username, Trabajador.class);
            return Optional.ofNullable(trabajador);
        } catch (IOException e) {
            System.err.println("Error al buscar trabajador por username: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Busca un trabajador por token de reset de contraseña
     */
    public Optional<Trabajador> findByResetPasswordToken(String token) {
        try {
            Trabajador trabajador = supabaseService.findByField(TABLE_NAME, "resetPasswordToken", token, Trabajador.class);
            return Optional.ofNullable(trabajador);
        } catch (IOException e) {
            System.err.println("Error al buscar trabajador por token: " + e.getMessage());
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
            System.err.println("Error al buscar todos los trabajadores: " + e.getMessage());
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
            System.err.println("Error al guardar trabajador: " + e.getMessage());
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
            System.err.println("Error al eliminar trabajador: " + e.getMessage());
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
            System.err.println("Error al contar trabajadores: " + e.getMessage());
            return 0;
        }
    }
}