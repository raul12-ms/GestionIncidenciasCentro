package com.example.gestionincidenciascentro.service;


import com.example.gestionincidenciascentro.DTO.IncidenciaResponse;
import com.example.gestionincidenciascentro.DTO.UsuarioResponse;
import com.example.gestionincidenciascentro.entity.Incidencia;
import com.example.gestionincidenciascentro.entity.Usuario;
import com.example.gestionincidenciascentro.exception.RolNoValidoException;
import com.example.gestionincidenciascentro.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioService {

    // Lista blanca: solo se acepta lo que está aquí, todo lo demás se rechaza
    private static final Set<String> ROLES_ADMITIDOS = Set.of("USER", "TECNICO", "ADMIN");

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    // Devuelve todos los usuarios de la BD (solo datos que se pueden mostrar)
    public List<UsuarioResponse> getUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    // Query Method derivado (findByNombreContainingIgnoreCase): el filtro lo hace MySQL (WHERE UPPER(nombre) LIKE UPPER('%texto%'))
    // Devuelve todos los usuarios cuyo nombre contenga el texto especificado (sin importar mayúsculas/minúsculas)
    public List<UsuarioResponse> buscarUsuariosPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::toResponse)
                .toList();
    }

    // Modifica el rol de un usuario por su id. Devuelve Optional.empty() si no existe el usuario.
    public Optional<UsuarioResponse> modifyRol(String id, String rol) {

        // 1. Validar ANTES de consultar la BD. Se comprueba null porque Set.of().contains(null) lanza NullPointerException
        if (rol == null || !ROLES_ADMITIDOS.contains(rol)) {
            throw new RolNoValidoException("Rol no admitido: " + rol); // 400 bad request
        }

        // 2. Cargar el usuario, cambiar SOLO el rol y guardar
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setRoles("ROLE_" + rol); // hasRole('TECNICO') busca "ROLE_TECNICO"
            Usuario editada = usuarioRepository.save(usuario);
            return toResponse(editada);
        }); // vacío > 404 en el controlador
    }

    // Metodo privado para convertir un Usuario en un UsuarioResponse. DTO de salida (solo datos que se pueden mostrar)
    private UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellidos(usuario.getApellidos())
                .build();
    }
}
