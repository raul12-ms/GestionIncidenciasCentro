package com.example.gestionincidenciascentro.exception;

public class EmailRepetidoException extends RuntimeException {

    public EmailRepetidoException() {
        super("El email ya está registrado");
    }

    // Constructor que permite pasar un mensaje personalizado y una causa (otra excepción) al crear la excepción.
    public EmailRepetidoException(String message, Throwable cause) {
        super(message, cause);
    }
    public EmailRepetidoException(Throwable cause) {
        super("El email ya está registrado", cause);
    }
}
