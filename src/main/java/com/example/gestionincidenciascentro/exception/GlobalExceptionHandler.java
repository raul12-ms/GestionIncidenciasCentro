package com.example.gestionincidenciascentro.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailRepetidoExpepcion.class)
    public ResponseEntity<Map<String, String>> handleEmailRepetido(EmailRepetidoExpepcion exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", exception.getMessage()));
        // Devuelve 409 Conflict con un mensaje de error en el cuerpo de la respuesta
    }


    /*
     * ¡OJO para el Ejercicio! NO añadas aquí un @ExceptionHandler(Exception.class) genérico:
     * capturaría también la AccessDeniedException que lanza @PreAuthorize y devolvería 500 en lugar
     * de 403 Forbidden. Ahora mismo esa excepción no la captura nadie aquí y Spring Security responde 403.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors); // Devuelve 400 Bad Request con los errores de validación
    }
}
