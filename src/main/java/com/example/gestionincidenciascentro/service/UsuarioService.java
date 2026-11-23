package com.example.gestionincidenciascentro.service;


import com.example.gestionincidenciascentro.DTO.UsuarioResponse;
import com.example.gestionincidenciascentro.entity.Usuario;
import com.example.gestionincidenciascentro.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    // Query Method derivado: el filtro lo hace MySQL (WHERE UPPER(nombre) LIKE UPPER('%texto%'))
    public List<UsuarioResponse> buscarUsuariosPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<UsuarioResponse> getUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    // Entidad -> DTO de salida (solo datos que se pueden mostrar)
    private UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellidos(usuario.getApellidos())
                .build();
    }
}
