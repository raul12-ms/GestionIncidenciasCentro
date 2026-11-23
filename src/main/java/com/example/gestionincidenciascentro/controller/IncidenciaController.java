package com.example.gestionincidenciascentro.controller;


import com.example.gestionincidenciascentro.DTO.IncidenciaRequest;
import com.example.gestionincidenciascentro.DTO.IncidenciaResponse;
import com.example.gestionincidenciascentro.entity.Incidencia;
import com.example.gestionincidenciascentro.service.IncidenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    // Inyección de dependencias por constructor, sin @Autowired
    public IncidenciaController(IncidenciaService incidenciaService) {

        this.incidenciaService = incidenciaService;
    }

    // GET: http://localhost:8080/incidencias -> devuelve todas las incidencias en la BD según el rol del usuario autenticado
    @PreAuthorize("hasAnyRole('USER','TECNICO','ADMIN')")
    @GetMapping("/incidencias")
    public List<Incidencia> findAll(Authentication authentication) {
        return incidenciaService.findAll(authentication); // Devuelve 200 OK con la lista de incidencias

    }

    // GET: http://localhost:8080/incidencias/{id} -> devuelve la incidencia con el id especificado según el rol del usuario autenticado
    @PreAuthorize("hasAnyRole('USER','TECNICO','ADMIN')")
    @GetMapping("/incidencias/{id}")
    public ResponseEntity<Incidencia> findById(@PathVariable Long id, Authentication authentication) {
        return incidenciaService.findById(id, authentication)
                .map(ResponseEntity::ok) // Devuelve 200 OK con la incidencia si se encuentra y el usuario tiene permiso
                .orElse(ResponseEntity.notFound().build()); // Devuelve 404 Not Found si no se encuentra la incidencia
    }

    // Spring inyecta el Authentication del usuario que hace la petición.
    // Con JWT es un JwtAuthenticationToken y getName() devuelve el "sub" del token (el email).
    // POST: http://localhost:8080/incidencias -> crea una nueva incidencia según el rol del usuario autenticado
    @PreAuthorize("hasAnyRole('USER','TECNICO','ADMIN')")
    @PostMapping("/incidencias") // @Valid se encarga de validar los campos del DTO IncidenciaRequest según las anotaciones de validación (@NotBlank, @Size, etc.)
    public ResponseEntity<IncidenciaResponse> create(@Valid @RequestBody IncidenciaRequest incidenciaRequest, Authentication authentication) {

        // Al crear una incidencia, obtener el autor (email) a partir de la autenticación
        return incidenciaService.create(incidenciaRequest, authentication.getName())
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved)) // Devuelve 201 Created con la incidencia creada
                .orElseGet(() -> ResponseEntity.notFound().build()); // Devuelve 404 Not Found si el autor no existe
    }

    // DELETE: http://localhost:8080/incidencias/{id} -> borra la incidencia con el id especificado. Solo ADMIN puede borrar incidencias.
    @PreAuthorize("hasRole('ADMIN')") // Spring lo hace automáticamente, si no tiene un rol válido -> 403 Forbidden.
    @DeleteMapping("/incidencias/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!incidenciaService.deleteById(id)) {
            return ResponseEntity.notFound().build();  // Devuelve 404 Not Found si no se encuentra la matrícula
        }
        return ResponseEntity.noContent().build(); // Devuelve 204 No Content si se borra correctamente
    }


//    @PreAuthorize("hasAnyRole('USER','SECRETARIA','ADMIN')")
//    @GetMapping("/cursos/{cursoId}/incidencias")
//    public ResponseEntity<List<Incidencia>> findByCurso(@PathVariable Long cursoId) {
//        return incidenciaService.findByCurso(cursoId)
//                .map(ResponseEntity::ok) // Devuelve 200 OK con la lista de matrículas del curso
//                .orElseGet(() -> ResponseEntity.notFound().build()); // Devuelve 404 Not Found si el curso no existe
//    }
//
//    @PreAuthorize("hasAnyRole('USER','SECRETARIA','ADMIN')")
//    @GetMapping("/alumnos/{alumnoId}/incidencias")
//    public ResponseEntity<List<Incidencia>> findByAlumno(@PathVariable Long alumnoId) {
//        return incidenciaService.findByAlumno(alumnoId)
//                .map(ResponseEntity::ok)  // Devuelve 200 OK con la lista de matrículas del alumno
//                .orElseGet(() -> ResponseEntity.notFound().build()); // Devuelve 404 Not Found si el alumno no existe
//    }
//
//    @PreAuthorize("hasAnyRole('USER','SECRETARIA','ADMIN')")
//    @GetMapping("/incidencias/resumen")
//    public ResponseEntity<?> getIncidenciasResumen() {
//        return ResponseEntity.ok(incidenciaService.getIncidenciasResumen()); // Devuelve 200 OK con el resumen de matrículas
//    }

}
