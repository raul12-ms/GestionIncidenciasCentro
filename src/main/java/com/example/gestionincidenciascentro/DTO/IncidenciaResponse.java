package com.example.gestionincidenciascentro.DTO;


import com.example.gestionincidenciascentro.entity.Incidencia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidenciaResponse {

    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaRegistro;
    private Incidencia.Estado estado;
    private String autorEmail;     // solo lo necesario del autor
    private String autorNombre;
}
