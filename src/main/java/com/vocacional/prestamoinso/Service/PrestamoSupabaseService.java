package com.vocacional.prestamoinso.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vocacional.prestamoinso.Entity.Cliente;
import com.vocacional.prestamoinso.Entity.Prestamo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PrestamoSupabaseService {

    private static final Logger logger = LoggerFactory.getLogger(PrestamoSupabaseService.class);

    @Autowired
    private SupabaseService supabaseService;

    private static final String TABLE_NAME = "prestamos";
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Busca un préstamo por ID
     */
    public Optional<Prestamo> findById(Long id) {
        try {
            Prestamo prestamo = supabaseService.findById(TABLE_NAME, id, Prestamo.class);
            return Optional.ofNullable(prestamo);
        } catch (IOException e) {
            logger.error("Error al buscar préstamo por ID: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Busca préstamos por número de documento del cliente
     */
    public List<Prestamo> findByCliente_NroDocumento(String dni) {
        try {
            return supabaseService.findAllByField(TABLE_NAME, "nroDocumento", dni, Prestamo.class);
        } catch (IOException e) {
            logger.error("Error al buscar préstamos por DNI: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Obtiene el total de préstamos mensuales para un cliente específico
     * Nota: Esta consulta compleja requiere una implementación personalizada
     */
    public Double obtenerTotalPrestamosMensuales(Long clienteId, int anio, int mes) {
        try {
            // Construir filtro para año y mes específicos
            String filter = String.format("cliente_id=eq.%d&fechaCreacion=gte.%d-%02d-01&fechaCreacion=lt.%d-%02d-01", 
                clienteId, anio, mes, anio, mes + 1);
            
            String result = supabaseService.executeCustomQuery(TABLE_NAME, "monto", filter);
            JsonNode jsonNode = objectMapper.readTree(result);
            
            double total = 0.0;
            if (jsonNode.isArray()) {
                for (JsonNode node : jsonNode) {
                    if (node.has("monto")) {
                        total += node.get("monto").asDouble();
                    }
                }
            }
            return total;
        } catch (IOException e) {
            logger.error("Error al obtener total de préstamos mensuales: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    /**
     * Busca todos los préstamos ordenados por fecha de creación descendente
     */
    public List<Prestamo> findAllByOrderByFechaCreacionDesc() {
        try {
            return supabaseService.findAllOrderByFechaCreacionDesc(TABLE_NAME, Prestamo.class);
        } catch (IOException e) {
            logger.error("Error al buscar préstamos ordenados: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Busca préstamos por cliente y estado
     */
    public List<Prestamo> findByClienteAndEstado(Cliente cliente, String estado) {
        try {
            return supabaseService.findAllByTwoFields(TABLE_NAME, "cliente_id", cliente.getId().toString(), "estado", estado, Prestamo.class);
        } catch (IOException e) {
            logger.error("Error al buscar préstamos por cliente y estado: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Busca préstamos por estado
     */
    public List<Prestamo> findByEstado(String estado) {
        try {
            return supabaseService.findAllByField(TABLE_NAME, "estado", estado, Prestamo.class);
        } catch (IOException e) {
            logger.error("Error al buscar préstamos por estado: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Busca todos los préstamos
     */
    public List<Prestamo> findAll() {
        try {
            return supabaseService.findAll(TABLE_NAME, Prestamo.class);
        } catch (IOException e) {
            logger.error("Error al buscar todos los préstamos: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Guarda un préstamo (insert o update)
     */
    public Prestamo save(Prestamo prestamo) {
        try {
            if (prestamo.getId() == null) {
                // Insert
                String result = supabaseService.insert(TABLE_NAME, prestamo);
                return supabaseService.parseJsonToObject(result, Prestamo.class);
            } else {
                // Update
                return supabaseService.updateById(TABLE_NAME, prestamo.getId(), prestamo, Prestamo.class);
            }
        } catch (IOException e) {
            logger.error("Error al guardar préstamo: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Elimina un préstamo por ID
     */
    public void deleteById(Long id) {
        try {
            supabaseService.deleteById(TABLE_NAME, id);
        } catch (IOException e) {
            logger.error("Error al eliminar préstamo: {}", e.getMessage(), e);
        }
    }

    /**
     * Elimina un préstamo
     */
    public void delete(Prestamo prestamo) {
        if (prestamo.getId() != null) {
            deleteById(prestamo.getId());
        }
    }

    /**
     * Cuenta el número total de préstamos
     */
    public long count() {
        try {
            List<Prestamo> prestamos = findAll();
            return prestamos.size();
        } catch (Exception e) {
            logger.error("Error al contar préstamos: {}", e.getMessage(), e);
            return 0L;
        }
    }
}