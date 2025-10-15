package com.vocacional.prestamoinso.Service;

import com.vocacional.prestamoinso.Entity.Cliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteSupabaseService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteSupabaseService.class);

    @Autowired
    private SupabaseService supabaseService;

    private static final String TABLE_NAME = "clientes";

    /**
     * Busca un cliente por ID
     */
    public Optional<Cliente> findById(Long id) {
        try {
            Cliente cliente = supabaseService.findById(TABLE_NAME, id, Cliente.class);
            return Optional.ofNullable(cliente);
        } catch (IOException e) {
            logger.error("Error al buscar cliente por ID: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Busca un cliente por número de documento
     */
    public Cliente findByNroDocumento(String nroDocumento) {
        try {
            return supabaseService.findByField(TABLE_NAME, "nroDocumento", nroDocumento, Cliente.class);
        } catch (IOException e) {
            logger.error("Error al buscar cliente por número de documento: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Verifica si existe un cliente con el número de documento dado
     */
    public boolean existsByNroDocumento(String nroDocumento) {
        try {
            return supabaseService.existsByField(TABLE_NAME, "nroDocumento", nroDocumento);
        } catch (IOException e) {
            logger.error("Error al verificar existencia de cliente: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Busca todos los clientes
     */
    public List<Cliente> findAll() {
        try {
            return supabaseService.findAll(TABLE_NAME, Cliente.class);
        } catch (IOException e) {
            logger.error("Error al buscar todos los clientes: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Guarda un cliente (insert o update)
     */
    public Cliente save(Cliente cliente) {
        try {
            if (cliente.getId() == null) {
                // Insert
                String result = supabaseService.insert(TABLE_NAME, cliente);
                return supabaseService.parseJsonToObject(result, Cliente.class);
            } else {
                // Update
                return supabaseService.updateById(TABLE_NAME, cliente.getId(), cliente, Cliente.class);
            }
        } catch (IOException e) {
            logger.error("Error al guardar cliente: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Elimina un cliente por ID
     */
    public void deleteById(Long id) {
        try {
            supabaseService.deleteById(TABLE_NAME, id);
        } catch (IOException e) {
            logger.error("Error al eliminar cliente: {}", e.getMessage(), e);
        }
    }

    /**
     * Elimina un cliente
     */
    public void delete(Cliente cliente) {
        if (cliente.getId() != null) {
            deleteById(cliente.getId());
        }
    }

    /**
     * Cuenta el número total de clientes
     */
    public long count() {
        try {
            List<Cliente> clientes = findAll();
            return clientes.size();
        } catch (Exception e) {
            logger.error("Error al contar clientes: {}", e.getMessage(), e);
            return 0L;
        }
    }
}