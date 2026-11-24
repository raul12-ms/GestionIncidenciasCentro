package com.example.gestionincidenciascentro.DTO;

import com.example.gestionincidenciascentro.entity.Incidencia;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// TODO: Deberia usar este DTO para cambiar el estado de una incidencia,
// en lugar de pasar directamente Incidencia.Estado en el cuerpo de la petición.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoRequest {
    // {"estado":"EN_PROCESO"}
    // @NotNull y no @NotBlank: @NotBlank solo sirve para textos
    @NotNull(message = "El estado es obligatorio")
    private Incidencia.Estado estado;
}
