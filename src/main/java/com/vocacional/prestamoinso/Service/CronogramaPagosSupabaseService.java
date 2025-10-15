package com.vocacional.prestamoinso.Service;

import com.vocacional.prestamoinso.Entity.CronogramaPagos;
import com.vocacional.prestamoinso.Entity.Prestamo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CronogramaPagosSupabaseService {

    private static final Logger logger = LoggerFactory.getLogger(CronogramaPagosSupabaseService.class);

    @Autowired
    private SupabaseService supabaseService;

    private static final String TABLE_NAME = "cronograma_pagos";

    /**
     * Busca un cronograma de pagos por ID
     */
    public Optional<CronogramaPagos> findById(Long id) {
        try {
            CronogramaPagos cronogramaPagos = supabaseService.findById(TABLE_NAME, id, CronogramaPagos.class);
            return Optional.ofNullable(cronogramaPagos);
        } catch (IOException e) {
            logger.error("Error al buscar cronograma de pagos por ID: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Guarda un cronograma de pagos
     */
    public CronogramaPagos save(CronogramaPagos cronogramaPagos) {
        try {
            if (cronogramaPagos.getId() == null) {
                String result = supabaseService.insert(TABLE_NAME, cronogramaPagos);
                return supabaseService.parseJsonToObject(result, CronogramaPagos.class);
            } else {
                return supabaseService.updateById(TABLE_NAME, cronogramaPagos.getId(), cronogramaPagos, CronogramaPagos.class);
            }
        } catch (IOException e) {
            logger.error("Error al guardar cronograma de pagos: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Busca cronograma de pagos por prestamo ID y estado ordenado por fecha de pago ascendente
     */
    public List<CronogramaPagos> findByPrestamoIdAndEstadoOrderByFechaPagoAsc(Long prestamoId, String estado) {
        try {
            String filter = "prestamo_id=eq." + prestamoId + "&estado=eq." + estado + "&order=fecha_pago.asc";
            return supabaseService.findByFilter(TABLE_NAME, filter, CronogramaPagos.class);
        } catch (IOException e) {
            logger.error("Error al buscar cronograma por prestamo ID y estado: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Busca cronograma de pagos por prestamo ID
     */
    public List<CronogramaPagos> findByPrestamoId(Long prestamoId) {
        try {
            String filter = "prestamo_id=eq." + prestamoId;
            return supabaseService.findByFilter(TABLE_NAME, filter, CronogramaPagos.class);
        } catch (IOException e) {
            logger.error("Error al buscar cronograma por prestamo ID: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Busca cronograma de pagos por prestamo y estado
     */
    public List<CronogramaPagos> findByPrestamoAndEstado(Prestamo prestamo, String estado) {
        try {
            String filter = "prestamo_id=eq." + prestamo.getId() + "&estado=eq." + estado;
            return supabaseService.findByFilter(TABLE_NAME, filter, CronogramaPagos.class);
        } catch (IOException e) {
            logger.error("Error al buscar cronograma por prestamo y estado: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Obtiene el total de pagos realizados para un préstamo específico
     */
    public Double obtenerTotalPagosPrestamo(Long prestamoId) {
        try {
            String filter = "prestamo_id=eq." + prestamoId + "&estado=eq.Pagado";
            List<CronogramaPagos> pagosPagados = supabaseService.findByFilter(TABLE_NAME, filter, CronogramaPagos.class);
            return pagosPagados.stream()
                    .mapToDouble(CronogramaPagos::getMontoCuota)
                    .sum();
        } catch (IOException e) {
            logger.error("Error al obtener total de pagos del prestamo: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    /**
     * Guarda una lista de cronograma de pagos
     */
    public List<CronogramaPagos> saveAll(List<CronogramaPagos> cronogramaPagosList) {
        List<CronogramaPagos> savedList = new ArrayList<>();
        for (CronogramaPagos cronogramaPagos : cronogramaPagosList) {
            CronogramaPagos saved = save(cronogramaPagos);
            if (saved != null) {
                savedList.add(saved);
            }
        }
        return savedList;
    }

    /**
     * Elimina un cronograma de pagos por ID
     */
    public void deleteById(Long id) {
        try {
            supabaseService.deleteById(TABLE_NAME, id);
        } catch (IOException e) {
            logger.error("Error al eliminar cronograma de pagos: {}", e.getMessage(), e);
        }
    }

    /**
     * Busca todos los cronogramas de pagos
     */
    public List<CronogramaPagos> findAll() {
        try {
            return supabaseService.findAll(TABLE_NAME, CronogramaPagos.class);
        } catch (IOException e) {
            logger.error("Error al buscar todos los cronogramas de pagos: {}", e.getMessage(), e);
            return List.of();
        }
    }
}