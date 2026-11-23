package com.example.gestionincidenciascentro.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO para la solicitud de inicio de sesión, que contiene el correo electrónico y la contraseña del usuario.
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {
}
