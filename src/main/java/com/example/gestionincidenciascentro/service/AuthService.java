package com.example.gestionincidenciascentro.service;


import com.example.gestionincidenciascentro.DTO.UsuarioRequest;
import com.example.gestionincidenciascentro.DTO.UsuarioResponse;
import com.example.gestionincidenciascentro.entity.Usuario;
import com.example.gestionincidenciascentro.exception.EmailRepetidoExpepcion;
import com.example.gestionincidenciascentro.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    // PasswordEncoder se utiliza para codificar la contraseña del usuario antes de guardarla en la base de datos.
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registra un nuevo usuario en la base de datos y devuelve un DTO con la información del usuario registrado.
    public UsuarioResponse register(UsuarioRequest usuarioRequest) {

        // Comprobar si el email ya está registrado en la base de datos (ignorando mayúsculas/minúsculas)
        if (usuarioRepository.findByEmailIgnoreCase(usuarioRequest.getEmail()).isPresent()) {
            throw new EmailRepetidoExpepcion();
        }

        Usuario usuario = Usuario.builder()
                .apellidos(usuarioRequest.getApellidos())
                .email(usuarioRequest.getEmail())
                .fechaNacimiento(usuarioRequest.getFechaNacimiento())
                .nombre(usuarioRequest.getNombre())
                .password(passwordEncoder.encode(usuarioRequest.getPassword()))
                .roles("ROLE_USER") // Asignar el rol de usuario por defecto
                .activo(true)
                .build();
        usuarioRepository.saveAndFlush(usuario);

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellidos(usuario.getApellidos())
                .build();
    }
}
