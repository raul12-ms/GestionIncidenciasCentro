package com.example.gestionincidenciascentro.service;

import com.example.gestionincidenciascentro.entity.Usuario;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMillis;

    // Constructor que recibe la clave secreta y el tiempo de expiración en horas desde application.properties
    public JwtService(
            @Value("${jwt.secret}") String configuredSecret,
            @Value("${jwt.expiration-hours:24}") long expirationHours) {

        byte[] secretBytes = configuredSecret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalArgumentException("jwt.secret debe tener al menos 32 bytes para HS256");
        }
        if (expirationHours <= 0) {
            throw new IllegalArgumentException("jwt.expiration-hours debe ser mayor que 0");
        }
        this.secretKey = Keys.hmacShaKeyFor(secretBytes);
        this.expirationMillis = expirationHours * 3600000L;
    }

    // Genera un token JWT para un usuario autenticado
    public String generateToken(Authentication authentication) {
        // Roles del usuario como lista: ["ROLE_USER", "ROLE_ADMIN", "ROLE_TECNICO"]
        // Filtramos por ROLE_ porque Spring Security también añade "FACTOR_PASSWORD" al hacer login
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .toList();

        // Construir el token JWT
        JwtBuilder builder = Jwts.builder()
                .subject(authentication.getName())  // Email del usuario
                .issuer("App-Matriculas")       // Quién emite el token
                .issuedAt(new Date())           // Cuándo se creó
                .expiration(new Date(System.currentTimeMillis() + expirationMillis)) // Cuándo expira
                .claim("roles", roles);         // Roles del usuario

        // Añadir el nombre del usuario al token solo si no es null ni vacío (claim adicional no sensible)
        if (authentication.getPrincipal() instanceof Usuario usuario
                && usuario.getNombre() != null
                && !usuario.getNombre().isBlank()) {
            builder.claim("nombre", usuario.getNombre());
        }

        return builder
                .signWith(secretKey)  // Firmar con la clave secreta
                .compact(); // Genera el token en formato String
    }

    public SecretKey getSecretKey() {

        return secretKey;
    }

}
