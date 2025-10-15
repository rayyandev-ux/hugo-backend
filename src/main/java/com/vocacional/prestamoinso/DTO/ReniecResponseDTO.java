package com.vocacional.prestamoinso.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class ReniecResponseDTO {
    @JsonAlias("first_name")
    private String nombres;
    
    @JsonAlias("first_last_name")
    private String apellidoPaterno;
    
    @JsonAlias("second_last_name")
    private String apellidoMaterno;
    
    @JsonAlias("document_number")
    private String numeroDocumento;
}
