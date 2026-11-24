package com.example.gestionincidenciascentro.config;

import com.example.gestionincidenciascentro.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;   // Para reglas por método HTTP: requestMatchers(HttpMethod.GET, "/alumnos/**")
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;


// Configuración de seguridad de Spring Security,
// aqui se definen las reglas de acceso a la API REST y cómo validar los tokens JWT
@Configuration  // Indica que esta clase es un bean de configuración de Spring
@EnableWebSecurity  // Habilita la seguridad web de Spring Security
@EnableMethodSecurity  // Permite usar @PreAuthorize en controladores
@RequiredArgsConstructor  // Genera un constructor con todos los campos final (para inyección de dependencias)
public class SecurityConfig {

    private final JwtService jwtService;

    // Las autorizaciones primero se comprueban en el SecurityFilterChain (de forma global)
    // y luego se comprueban las de a nivel de metodo con @PreAuthorize de los controladores de forma específica.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())  // esto permite que el frontend pueda hacer peticiones a la API REST (CorsConfig)
            // Deshabilitar CSRF (no necesario en APIs REST con JWT)
            .csrf(csrf -> csrf.disable())

            // Configurar autorización de peticiones HTTP
            // Define qué rutas son públicas y cuáles requieren autenticación
            // Hay que poner las reglas más específicas antes que las generales, porque Spring Security
            // evalúa en orden y la primera que coincide se aplica.
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/auth/**").permitAll()  // /auth/login y /auth/register son públicos
                    .requestMatchers("/error").permitAll()   // para ver errores reales (practica común)
                    .requestMatchers("/usuarios/**").hasRole("ADMIN")  // Solo ADMIN puede acceder a /usuarios/**
                    .requestMatchers(HttpMethod.DELETE, "/incidencias/**").hasRole("ADMIN")  // específica: ANTES que la general
                    .requestMatchers(HttpMethod.PATCH, "/incidencias/**").hasAnyRole("TECNICO", "ADMIN")
                    //.requestMatchers("/usuarios/miperfil").authenticated()  // Cualquier usuario autenticado puede acceder a su perfil
                    //.requestMatchers(HttpMethod.DELETE,"/usuarios/{usuarioId}/detalle-usuario").hasAnyRole("ADMIN", "DIRECTOR","JEFATURA","SECRETARIA") // Solo ADMIN, DIRECTOR, JEFATURA y SECRETARIA pueden eliminar detalle de usuario
                    .anyRequest().authenticated()  // Todas las demás rutas requieren autenticación (siempre la última)
            )

            // Configurar validación automática de tokens JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt
                            .decoder(jwtDecoder())  // Cómo validar el token
                            .jwtAuthenticationConverter(jwtAuthenticationConverter())  // Cómo extraer roles
                    )
            )

            // Sin sesiones (stateless) - cada petición debe llevar su token
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Valida la firma con la clave secreta
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtService.getSecretKey()).build();
        // Valida además la expiración y que el token lo haya emitido nuestra API
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("App-Matriculas"));
        return decoder;
    }

    // Configura cómo extraer los roles del token y convertirlos en GrantedAuthority
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        // Configura cómo extraer los roles del token
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");  // Buscar en el claim "roles"
        authoritiesConverter.setAuthorityPrefix("");   // Sin prefijo: los roles ya traen "ROLE_"

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return jwtConverter;
    }

@Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt para cifrar contraseñas en la base de datos
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        // Necesario para validar email/password en el login
        return authConfig.getAuthenticationManager();
    }
}
