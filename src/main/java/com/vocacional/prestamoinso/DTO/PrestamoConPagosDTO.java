package com.vocacional.prestamoinso.DTO;

import com.vocacional.prestamoinso.Entity.Cliente;
import com.vocacional.prestamoinso.Entity.CronogramaPagos;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PrestamoConPagosDTO {
    private Long id;
    private String cliente; // Cambiar de Cliente a String para almacenar el nombre completo
    private String nroDocumento;
    private double monto;
    private double interes;
    private int plazo;
    private LocalDateTime fechaCreacion;
    private String estado;
    private List<CronogramaPagos> cronogramaPagos;
}