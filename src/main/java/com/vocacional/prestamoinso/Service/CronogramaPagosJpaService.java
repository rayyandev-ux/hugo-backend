package com.vocacional.prestamoinso.Service;

import com.vocacional.prestamoinso.Entity.CronogramaPagos;
import com.vocacional.prestamoinso.Entity.Prestamo;
import com.vocacional.prestamoinso.Repository.CronogramaPagosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CronogramaPagosJpaService {

    private static final Logger logger = LoggerFactory.getLogger(CronogramaPagosJpaService.class);

    @Autowired
    private CronogramaPagosRepository cronogramaPagosRepository;

    /**
     * Busca un cronograma de pagos por ID
     */
    public Optional<CronogramaPagos> findById(Long id) {
        try {
            return cronogramaPagosRepository.findById(id);
        } catch (Exception e) {
            logger.error("Error al buscar cronograma de pagos por ID: {}", id, e);
            return Optional.empty();
        }
    }

    /**
     * Guarda un cronograma de pagos
     */
    public CronogramaPagos save(CronogramaPagos cronogramaPagos) {
        try {
            return cronogramaPagosRepository.save(cronogramaPagos);
        } catch (Exception e) {
            logger.error("Error al guardar cronograma de pagos: {}", cronogramaPagos, e);
            throw new RuntimeException("Error al guardar cronograma de pagos", e);
        }
    }

    /**
     * Busca cronograma de pagos por prestamo ID y estado ordenado por fecha de pago ascendente
     */
    public List<CronogramaPagos> findByPrestamoIdAndEstadoOrderByFechaPagoAsc(Long prestamoId, String estado) {
        try {
            return cronogramaPagosRepository.findByPrestamoIdAndEstadoOrderByFechaPagoAsc(prestamoId, estado);
        } catch (Exception e) {
            logger.error("Error al buscar cronograma por prestamo ID y estado: {} - {}", prestamoId, estado, e);
            throw new RuntimeException("Error al buscar cronograma por prestamo ID y estado", e);
        }
    }

    /**
     * Busca cronograma de pagos por prestamo ID
     */
    public List<CronogramaPagos> findByPrestamoId(Long prestamoId) {
        try {
            return cronogramaPagosRepository.findByPrestamoId(prestamoId);
        } catch (Exception e) {
            logger.error("Error al buscar cronograma por prestamo ID: {}", prestamoId, e);
            throw new RuntimeException("Error al buscar cronograma por prestamo ID", e);
        }
    }

    /**
     * Busca cronograma de pagos por prestamo y estado
     */
    public List<CronogramaPagos> findByPrestamoAndEstado(Prestamo prestamo, String estado) {
        try {
            return cronogramaPagosRepository.findByPrestamoAndEstado(prestamo, estado);
        } catch (Exception e) {
            logger.error("Error al buscar cronograma por prestamo y estado: {} - {}", prestamo.getId(), estado, e);
            throw new RuntimeException("Error al buscar cronograma por prestamo y estado", e);
        }
    }

    /**
     * Obtiene el total de pagos realizados para un préstamo específico
     */
    public Double obtenerTotalPagosPrestamo(Long prestamoId) {
        try {
            Double total = cronogramaPagosRepository.obtenerTotalPagosPrestamo(prestamoId);
            return total != null ? total : 0.0;
        } catch (Exception e) {
            logger.error("Error al obtener total de pagos del prestamo: {}", prestamoId, e);
            return 0.0;
        }
    }

    /**
     * Guarda una lista de cronograma de pagos
     */
    public List<CronogramaPagos> saveAll(List<CronogramaPagos> cronogramaPagosList) {
        try {
            return cronogramaPagosRepository.saveAll(cronogramaPagosList);
        } catch (Exception e) {
            logger.error("Error al guardar lista de cronograma de pagos", e);
            throw new RuntimeException("Error al guardar lista de cronograma de pagos", e);
        }
    }

    /**
     * Elimina un cronograma de pagos por ID
     */
    public void deleteById(Long id) {
        try {
            cronogramaPagosRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error al eliminar cronograma de pagos con ID: {}", id, e);
            throw new RuntimeException("Error al eliminar cronograma de pagos", e);
        }
    }

    /**
     * Elimina un cronograma de pagos
     */
    public void delete(CronogramaPagos cronogramaPagos) {
        try {
            if (cronogramaPagos.getId() != null) {
                cronogramaPagosRepository.deleteById(cronogramaPagos.getId());
            }
        } catch (Exception e) {
            logger.error("Error al eliminar cronograma de pagos: {}", cronogramaPagos, e);
            throw new RuntimeException("Error al eliminar cronograma de pagos", e);
        }
    }

    /**
     * Busca todos los cronogramas de pagos
     */
    public List<CronogramaPagos> findAll() {
        try {
            return cronogramaPagosRepository.findAll();
        } catch (Exception e) {
            logger.error("Error al buscar todos los cronogramas de pagos", e);
            throw new RuntimeException("Error al obtener cronogramas de pagos", e);
        }
    }

    /**
     * Cuenta el número total de cronogramas de pagos
     */
    public long count() {
        try {
            return cronogramaPagosRepository.count();
        } catch (Exception e) {
            logger.error("Error al contar cronogramas de pagos", e);
            return 0L;
        }
    }
}