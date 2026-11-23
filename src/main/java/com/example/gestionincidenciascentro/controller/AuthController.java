package com.example.gestionincidenciascentro.controller;


import com.example.gestionincidenciascentro.DTO.LoginRequest;
import com.example.gestionincidenciascentro.DTO.UsuarioRequest;
import com.example.gestionincidenciascentro.DTO.UsuarioResponse;
import com.example.gestionincidenciascentro.service.AuthService;
import com.example.gestionincidenciascentro.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // @Valid se encarga de validar los campos del DTO LoginRequest según las anotaciones de validación (@NotBlank, @Email)
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Autenticar al usuario con email y password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
            );

            // Si las credenciales son correctas, generar token y se lo devolvemos al cliente en el cuerpo de la respuesta
            String token = jwtService.generateToken(authentication);
            return ResponseEntity.ok(Map.of("token", token)); // Devuelve 200 OK con el token JWT en el cuerpo de la respuesta

        } catch (AuthenticationException e) {
            // Contraseña incorrecta, usuario inexistente o cuenta deshabilitada
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales incorrectas")); // Devuelve 401 Unauthorized con un mensaje de error
        }
    }


    // @Valid se encarga de validar los campos del DTO UsuarioRequest según las anotaciones de validación (@NotBlank, @Email, @Past)
    @PostMapping("/register")
    // el servicio: lanza EmailRepetidoExpepcion y GlobalExceptionHandler la convierte en 409 Conflict.
    // Flujo: @Valid falla -> 400 | email repetido -> 409 | correcto -> 201 con UsuarioResponse.
    public ResponseEntity<UsuarioResponse> register(@Valid @RequestBody UsuarioRequest usuarioRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(usuarioRequest));
        // Devuelve 201 Created con el DTO UsuarioResponse en el cuerpo de la respuesta
    }

}
