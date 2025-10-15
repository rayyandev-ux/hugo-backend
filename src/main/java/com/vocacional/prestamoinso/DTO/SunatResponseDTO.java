package com.vocacional.prestamoinso.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class SunatResponseDTO {
    @JsonAlias("razon_social")
    private String razonSocial;
    
    private String tipoDocumento;
    
    @JsonAlias("numero_documento")
    private String numeroDocumento;
    
    private String estado;
    private String condicion;
    private String direccion;
    private String ubigeo;
    
    @JsonAlias("via_tipo")
    private String viaTipo;
    
    @JsonAlias("via_nombre")
    private String viaNombre;
    
    @JsonAlias("zona_codigo")
    private String zonaCodigo;
    
    @JsonAlias("zona_tipo")
    private String zonaTipo;
    
    private String numero;
    private String interior;
    private String lote;
    private String dpto;
    private String manzana;
    private String kilometro;
    private String distrito;
    private String provincia;
    private String departamento;
    
    @JsonAlias("es_agente_retencion")
    private Boolean esAgenteRetencion;
    
    @JsonAlias("es_buen_contribuyente")
    private Boolean esBuenContribuyente;
    
    @JsonAlias("locales_anexos")
    private String localesAnexos;
    
    private String tipo;
    
    @JsonAlias("actividad_economica")
    private String actividadEconomica;
    
    @JsonAlias("numero_trabajadores")
    private String numeroTrabajadores;
    
    @JsonAlias("tipo_facturacion")
    private String tipoFacturacion;
    
    @JsonAlias("tipo_contabilidad")
    private String tipoContabilidad;
    
    @JsonAlias("comercio_exterior")
    private String comercioExterior;
}
