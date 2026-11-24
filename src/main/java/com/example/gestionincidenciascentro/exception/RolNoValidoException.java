package com.example.gestionincidenciascentro.exception;

// Excepción personalizada para indicar que un rol no es válido
public class RolNoValidoException extends RuntimeException {
    public RolNoValidoException(String message) {
        super(message);
    }
}
