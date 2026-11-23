package com.example.gestionincidenciascentro.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

// DTO para la solicitud de registro de un nuevo usuario, que contiene el nombre, apellidos,
// correo electrónico, fecha de nacimiento y contraseña del usuario.
@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class UsuarioRequest {

    @NotEmpty(message = "El campo nombre no puede estar vacío")
    private String nombre;

    private String apellidos;

    @Email (message = "El campo email debe ser un email válido")
    @NotEmpty (message = "El campo email no puede estar vacío")
    private String email;

    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
    private LocalDate fechaNacimiento;

    // @Size NO comprueba null: sin @NotBlank, un JSON sin "password" pasaba la validación
    // y BCrypt lanzaba IllegalArgumentException al cifrar null -> 500 en vez de 400.
    // @ToString.Exclude: @Data genera toString() y no queremos la contraseña en los logs.
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    @ToString.Exclude
    private String password;
}
