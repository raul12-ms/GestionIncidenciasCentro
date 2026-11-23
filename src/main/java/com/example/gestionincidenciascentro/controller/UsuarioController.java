package com.example.gestionincidenciascentro.controller;


import com.example.gestionincidenciascentro.DTO.UsuarioResponse;
import com.example.gestionincidenciascentro.service.UsuarioService;
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

    // GET: http://localhost:8080/usuarios/buscar?nombre=Juan -> devuelve todos los usuarios cuyo nombre contiene "Juan" (sin contraseña ni roles)
    @GetMapping("/buscar")
    // Devuelve el DTO UsuarioResponse, igual que GET /usuarios.
    public ResponseEntity<List<UsuarioResponse>> buscarUsuariosPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(usuarioService.buscarUsuariosPorNombre(nombre)); // Devuelve 200 OK con la lista de usuarios
    }


    // GET: http://localhost:8080/usuarios -> devuelve todos los usuarios en la BD (sin contraseña ni roles)
    @GetMapping()
    public ResponseEntity<?> getUsuarios() {
        return ResponseEntity.ok(usuarioService.getUsuarios()); // Devuelve 200 OK con la lista de usuarios

    }
}
