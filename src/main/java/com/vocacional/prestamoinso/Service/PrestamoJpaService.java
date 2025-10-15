package com.vocacional.prestamoinso.Service;

import com.vocacional.prestamoinso.Entity.Cliente;
import com.vocacional.prestamoinso.Entity.Prestamo;
import com.vocacional.prestamoinso.Repository.PrestamoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrestamoJpaService {

    private static final Logger logger = LoggerFactory.getLogger(PrestamoJpaService.class);

    @Autowired
    private PrestamoRepository prestamoRepository;

    /**
     * Busca un préstamo por ID
     */
    public Optional<Prestamo> findById(Long id) {
        try {
            return prestamoRepository.findById(id);
        } catch (Exception e) {
            logger.error("Error al buscar préstamo por ID: {}", id, e);
            return Optional.empty();
        }
    }

    /**
     * Busca préstamos por número de documento del cliente
     */
    public List<Prestamo> findByCliente_NroDocumento(String dni) {
        try {
            return prestamoRepository.findByCliente_NroDocumento(dni);
        } catch (Exception e) {
            logger.error("Error al buscar préstamos por DNI: {}", dni, e);
            throw new RuntimeException("Error al buscar préstamos por DNI", e);
        }
    }

    /**
     * Obtiene el total de préstamos mensuales para un cliente
     */
    public Double obtenerTotalPrestamosMensuales(Long clienteId, int anio, int mes) {
        try {
            return prestamoRepository.obtenerTotalPrestamosMensuales(clienteId, anio, mes);
        } catch (Exception e) {
            logger.error("Error al obtener total de préstamos mensuales para cliente: {}", clienteId, e);
            return 0.0;
        }
    }

    /**
     * Busca todos los préstamos ordenados por fecha de creación descendente
     */
    public List<Prestamo> findAllByOrderByFechaCreacionDesc() {
        try {
            return prestamoRepository.findAllByOrderByFechaCreacionDesc();
        } catch (Exception e) {
            logger.error("Error al buscar préstamos ordenados", e);
            throw new RuntimeException("Error al obtener préstamos ordenados", e);
        }
    }

    /**
     * Busca préstamos por cliente y estado
     */
    public List<Prestamo> findByClienteAndEstado(Cliente cliente, String estado) {
        try {
            return prestamoRepository.findByClienteAndEstado(cliente, estado);
        } catch (Exception e) {
            logger.error("Error al buscar préstamos por cliente y estado: {} - {}", cliente.getId(), estado, e);
            throw new RuntimeException("Error al buscar préstamos por cliente y estado", e);
        }
    }

    /**
     * Busca préstamos por estado
     */
    public List<Prestamo> findByEstado(String estado) {
        try {
            return prestamoRepository.findByEstado(estado);
        } catch (Exception e) {
            logger.error("Error al buscar préstamos por estado: {}", estado, e);
            throw new RuntimeException("Error al buscar préstamos por estado", e);
        }
    }

    /**
     * Busca todos los préstamos
     */
    public List<Prestamo> findAll() {
        try {
            return prestamoRepository.findAll();
        } catch (Exception e) {
            logger.error("Error al buscar todos los préstamos", e);
            throw new RuntimeException("Error al obtener préstamos", e);
        }
    }

    /**
     * Guarda un préstamo (insert o update)
     */
    public Prestamo save(Prestamo prestamo) {
        try {
            return prestamoRepository.save(prestamo);
        } catch (Exception e) {
            logger.error("Error al guardar préstamo: {}", prestamo, e);
            throw new RuntimeException("Error al guardar préstamo", e);
        }
    }

    /**
     * Elimina un préstamo por ID
     */
    public void deleteById(Long id) {
        try {
            prestamoRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error al eliminar préstamo con ID: {}", id, e);
            throw new RuntimeException("Error al eliminar préstamo", e);
        }
    }

    /**
     * Elimina un préstamo
     */
    public void delete(Prestamo prestamo) {
        try {
            if (prestamo.getId() != null) {
                prestamoRepository.deleteById(prestamo.getId());
            }
        } catch (Exception e) {
            logger.error("Error al eliminar préstamo: {}", prestamo, e);
            throw new RuntimeException("Error al eliminar préstamo", e);
        }
    }

    /**
     * Cuenta el número total de préstamos
     */
    public long count() {
        try {
            return prestamoRepository.count();
        } catch (Exception e) {
            logger.error("Error al contar préstamos", e);
            return 0L;
        }
    }
}