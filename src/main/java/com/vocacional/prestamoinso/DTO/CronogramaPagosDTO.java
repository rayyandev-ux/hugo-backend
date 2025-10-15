package com.vocacional.prestamoinso.DTO;


import com.vocacional.prestamoinso.Entity.Prestamo;
import lombok.Data;


import java.time.LocalDate;

@Data
public class CronogramaPagosDTO {
    private Long id;
    private Prestamo prestamo;
    private String estado;
    private LocalDate fechaPago;
    private double montoCuota;
    private double pagoIntereses;  // Nueva columna: pago de intereses
    private double amortizacion;   // Nueva columna: amortización
    private double saldoRestante;
}
