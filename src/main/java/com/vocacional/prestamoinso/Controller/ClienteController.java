package com.vocacional.prestamoinso.Controller;

import com.vocacional.prestamoinso.DTO.ClienteDTO;
import com.vocacional.prestamoinso.DTO.ReniecResponseDTO;
import com.vocacional.prestamoinso.DTO.SunatResponseDTO;
import com.vocacional.prestamoinso.Entity.Cliente;
import com.vocacional.prestamoinso.Exception.ConflictException;
import com.vocacional.prestamoinso.Service.ClienteJpaService;
import com.vocacional.prestamoinso.Service.ClienteService;
import com.vocacional.prestamoinso.Service.JwtUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private JwtUtilService jwtUtilService;

    @Autowired
    private ClienteJpaService clienteJpaService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarCliente(@RequestBody ClienteDTO registroClienteDTO) {
        try {
            Cliente cliente;
            // Validar que el DTO tenga el número de documento
            if (registroClienteDTO.getNroDocumento() == null || registroClienteDTO.getNroDocumento().trim().isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "El número de documento es requerido.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // Si el cliente ya existe, no lanzamos la excepción Conflict
            if (clienteJpaService.existsByNroDocumento(registroClienteDTO.getNroDocumento())) {
                cliente = clienteJpaService.findByNroDocumento(registroClienteDTO.getNroDocumento());
            } else {
                cliente = clienteService.registrarCliente(registroClienteDTO);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
        } catch (DataIntegrityViolationException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Ya existe un cliente con este email.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error interno del servidor.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }





    @GetMapping("/{dni}")
    public ResponseEntity<?> obtenerDatosCliente(@PathVariable String dni) {


        if (dni.length() == 11) {
            SunatResponseDTO datosCliente = clienteService.validarRUC(dni);
            if (datosCliente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // No se encontró el cliente en RENIEC
            }
            return ResponseEntity.ok(datosCliente);
        } else {
            ReniecResponseDTO datosCliente = clienteService.validarDNI(dni);
            if (datosCliente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // No se encontró el cliente en RENIEC
            }
            return ResponseEntity.ok(datosCliente);
        }
    }
}