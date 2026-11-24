package com.example.gestionincidenciascentro.controller;


import com.example.gestionincidenciascentro.DTO.IncidenciaResponse;
import com.example.gestionincidenciascentro.DTO.UsuarioResponse;
import com.example.gestionincidenciascentro.entity.Incidencia;
import com.example.gestionincidenciascentro.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Permite únicamente a un administrador consultar usuarios y modificar sus roles.
@RestController
@RequestMapping("/usuarios") // todas las rutas de este controlador empiezan con /usuarios
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {

        this.usuarioService = usuarioService;
    }


    // GET: http://localhost:8080/usuarios -> devuelve todos los usuarios en la BD (sin contraseña ni roles)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping() // Segunda barrera de seguridad: solo ADMIN puede buscar usuarios por nombre.
    public ResponseEntity<List<UsuarioResponse>> getUsuarios() {
        return ResponseEntity.ok(usuarioService.getUsuarios()); // Devuelve 200 OK con la lista de usuarios

    }

    // GET: http://localhost:8080/usuarios/buscar?nombre=Juan -> devuelve todos los usuarios cuyo nombre contiene "Juan" (sin contraseña ni roles)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/buscar")
    // Devuelve el DTO UsuarioResponse, igual que GET /usuarios.
    public ResponseEntity<List<UsuarioResponse>> buscarUsuariosPorNombre(@RequestParam String nombre) {
        List<UsuarioResponse> usuarios = usuarioService.buscarUsuariosPorNombre(nombre);
//        if (usuarios.isEmpty()) {
//            return ResponseEntity.notFound().build(); // Devuelve 404 Not Found si no hay usuarios que coincidan con el nombre buscado
//        } else {
            return ResponseEntity.ok(usuarios); // Devuelve 200 OK con la lista de usuarios
//        }

    }


    // PATCH: http://localhost:8080/usuarios/{id}/roles -> modifica los roles del usuario con el id especificado
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/roles")    // @Valid
    public ResponseEntity<UsuarioResponse> modifyRol(@PathVariable String id, @RequestBody String rol) {
        return usuarioService.modifyRol(id, rol)
                .map(ResponseEntity::ok) // Devuelve 200 OK con el usuario modificado
                .orElseGet(() -> ResponseEntity.notFound().build()); // Devuelve 404 Not Found si no se encuentra el usuario
    }
    
}
