package com.example.gestionincidenciascentro.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//Como no son entidades y no hay relaciones se puede usar @Data para generar los getters y setters
// Este DTO se usa para devolver información del usuario sin exponer la contraseña ni los roles
@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class UsuarioResponse {
    private String id;
    private String email;
    private String nombre;
    private String apellidos;
    // rol?
}
