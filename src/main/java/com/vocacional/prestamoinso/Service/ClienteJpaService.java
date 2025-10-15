package com.vocacional.prestamoinso.Service;

import com.vocacional.prestamoinso.Entity.Cliente;
import com.vocacional.prestamoinso.Repository.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteJpaService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteJpaService.class);

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Busca un cliente por ID
     */
    public Optional<Cliente> findById(Long id) {
        try {
            return clienteRepository.findById(id);
        } catch (Exception e) {
            logger.error("Error al buscar cliente por ID: {}", id, e);
            return Optional.empty();
        }
    }

    /**
     * Busca un cliente por número de documento
     */
    public Cliente findByNroDocumento(String nroDocumento) {
        try {
            return clienteRepository.findByNroDocumento(nroDocumento);
        } catch (Exception e) {
            logger.error("Error al buscar cliente por número de documento: {}", nroDocumento, e);
            return null;
        }
    }

    /**
     * Verifica si existe un cliente con el número de documento dado
     */
    public boolean existsByNroDocumento(String nroDocumento) {
        try {
            return clienteRepository.existsByNroDocumento(nroDocumento);
        } catch (Exception e) {
            logger.error("Error al verificar existencia de cliente: {}", nroDocumento, e);
            return false;
        }
    }

    /**
     * Busca todos los clientes
     */
    public List<Cliente> findAll() {
        try {
            return clienteRepository.findAll();
        } catch (Exception e) {
            logger.error("Error al buscar todos los clientes", e);
            throw new RuntimeException("Error al obtener clientes", e);
        }
    }

    /**
     * Guarda un cliente (insert o update)
     */
    public Cliente save(Cliente cliente) {
        try {
            return clienteRepository.save(cliente);
        } catch (Exception e) {
            logger.error("Error al guardar cliente: {}", cliente, e);
            throw new RuntimeException("Error al guardar cliente", e);
        }
    }

    /**
     * Elimina un cliente por ID
     */
    public void deleteById(Long id) {
        try {
            clienteRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error al eliminar cliente con ID: {}", id, e);
            throw new RuntimeException("Error al eliminar cliente", e);
        }
    }

    /**
     * Elimina un cliente
     */
    public void delete(Cliente cliente) {
        try {
            if (cliente.getId() != null) {
                clienteRepository.deleteById(cliente.getId());
            }
        } catch (Exception e) {
            logger.error("Error al eliminar cliente: {}", cliente, e);
            throw new RuntimeException("Error al eliminar cliente", e);
        }
    }

    /**
     * Cuenta el número total de clientes
     */
    public long count() {
        try {
            return clienteRepository.count();
        } catch (Exception e) {
            logger.error("Error al contar clientes", e);
            return 0L;
        }
    }
}