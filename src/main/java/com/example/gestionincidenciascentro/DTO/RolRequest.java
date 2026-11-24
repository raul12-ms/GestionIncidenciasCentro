package com.example.gestionincidenciascentro.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// TODO: Deberia de usar este DTO para recibir el rol en el cuerpo de la petición PATCH /usuarios/{id}/roles
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolRequest {
    // {"rol":"TECNICO"}
    @NotBlank(message = "El rol es obligatorio")
    private String rol;
}
