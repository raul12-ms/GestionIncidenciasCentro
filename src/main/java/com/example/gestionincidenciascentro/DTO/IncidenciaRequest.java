package com.example.gestionincidenciascentro.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidenciaRequest {
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 255) // Para evitar un error 500 si se intenta guardar un título demasiado largo en la bbdd
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 255)
    private String descripcion;
}