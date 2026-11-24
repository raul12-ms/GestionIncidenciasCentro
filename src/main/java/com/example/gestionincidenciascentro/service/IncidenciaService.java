package com.example.gestionincidenciascentro.service;

import com.example.gestionincidenciascentro.DTO.IncidenciaRequest;
import com.example.gestionincidenciascentro.DTO.IncidenciaResponse;
import com.example.gestionincidenciascentro.entity.Incidencia;
import com.example.gestionincidenciascentro.repository.IncidenciaRepository;

import com.example.gestionincidenciascentro.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final UsuarioRepository usuarioRepository;

    /*
     * Inyección de dependencias: SIEMPRE por constructor con atributos private final.
     * Formas equivalentes de hacerlo:
     *   1) Constructor escrito a mano (este). Si la clase tiene un único constructor,
     *      Spring lo usa automáticamente: NO hace falta poner @Autowired.
     *   2) @RequiredArgsConstructor de Lombok: genera este mismo constructor con los campos final
     *      (es lo que usan SecurityConfig y AuthController).
     *   3) @Autowired sobre el atributo (inyección por campo):
     *          @Autowired private IncidenciaRepository incidenciaRepository;
     *      Funciona, pero NO se recomienda: el campo no puede ser final, la dependencia queda
     *      oculta y la clase no se puede instanciar en un test sin Spring.
     */
    public IncidenciaService(IncidenciaRepository incidenciaRepository, UsuarioRepository usuarioRepository) {
        this.incidenciaRepository = incidenciaRepository;
        this.usuarioRepository = usuarioRepository;

    }

    // A partir del Authentication del usuario autenticado, devuelve true si tiene rol TECNICO o ADMIN
    private boolean puedeVerTodas(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TECNICO")
                        || a.getAuthority().equals("ROLE_ADMIN"));
    }

    // LISTADO: TECNICO/ADMIN > todas; USER > solo las suyas (filtra MySQL con Query, no con for/if)
    public List<IncidenciaResponse> findAll(Authentication authentication) {
        if (puedeVerTodas(authentication)) {
            return incidenciaRepository.findAll().stream()
                    .map(this::toResponse)
                    .toList();
        }
        return incidenciaRepository.findByAutorEmail(authentication.getName()).stream()
                .map(this::toResponse)
                .toList();
    }

    // UNA INCIDENCIA: TECNICO/ADMIN > cualquiera; USER > solo si el autor es él
    public Optional<IncidenciaResponse> findById(Long id, Authentication authentication) {
        if (puedeVerTodas(authentication)) {
            return incidenciaRepository.findById(id).map(incidencia -> {
                return toResponse(incidencia);  // this::toResponse
            });
        }
        return incidenciaRepository.findByIdAndAutorEmail(id, authentication.getName()).map(this::toResponse);
    }


    /*
     * Se recibe el email del usuario autenticado (el "sub" del JWT) y se asocia.
     * Aqui cualquiera puede crear incidencias (USER, TECNICO, ADMIN), pero el autor se asigna automáticamente al usuario autenticado.
     */
    public Optional<IncidenciaResponse> create(IncidenciaRequest request, String emailAutor) {
        // 1. Buscar al autor con el email que viene del TOKEN (si no existe - Optional vacío - 404)
        return usuarioRepository.findByEmail(emailAutor).map(autor -> {
            // 2. Construir la entidad: el cliente solo aporta título y descripción
            Incidencia incidencia = Incidencia.builder()
                    .titulo(request.getTitulo())
                    .descripcion(request.getDescripcion())
                    .estado(Incidencia.Estado.ABIERTA)  // Siempre se crea como ABIERTA
                    .autor(autor)          // del token, nunca del JSON
                    .build();
            // 3. Guardar la ENTIDAD (antes del INSERT, @PrePersist pone fechaCreacion)
            Incidencia guardada = incidenciaRepository.save(incidencia);
            // 4. Convertir la entidad guardada (ya con id y fecha) en el DTO de salida
            return toResponse(guardada);
        });

    }

    // PATCH: cambiar estado de una incidencia. Solo TECNICO/ADMIN pueden cambiar el estado.
    public Optional<IncidenciaResponse> modifyEstado(Long id, Incidencia.Estado nuevoEstado) {
        return incidenciaRepository.findById(id).map(incidencia -> {
            incidencia.setEstado(nuevoEstado);
            Incidencia editada = incidenciaRepository.save(incidencia);
            return toResponse(editada);
        });
    }


    // Entidad > DTO. Es privado porque solo lo usa este servicio; lo reutilizarás en los GET y en cambiar estado.
    private IncidenciaResponse toResponse(Incidencia incidencia) {
        return IncidenciaResponse.builder()
                .id(incidencia.getId())
                .titulo(incidencia.getTitulo())
                .descripcion(incidencia.getDescripcion())
                .fechaRegistro(incidencia.getFechaRegistro())
                .estado(incidencia.getEstado())
                .autorEmail(incidencia.getAutor().getEmail())
                .autorNombre(incidencia.getAutor().getNombre())
                .build();
    }


    // BORRAR: ADMIN > cualquiera. Solo el ADMIN puede borrar incidencias
    public boolean deleteById(Long id) {
        if (!incidenciaRepository.existsById(id)) {
            return false;
        }
        incidenciaRepository.deleteById(id);
        return true;
    }


//    public Optional<List<Incidencia>> findByCurso(Long cursoId) {
//        if (!cursoRepository.existsById(cursoId)) {
//            return Optional.empty();
//        }
//        return Optional.of(incidenciaRepository.findByCursoId(cursoId));
//    }
//
//    public Optional<List<Incidencia>> findByAlumno(Long alumnoId) {
//        if (!alumnoRepository.existsById(alumnoId)) {
//            return Optional.empty();
//        }
//        return Optional.of(incidenciaRepository.findByAlumnoId(alumnoId));
//    }

    // [REVISIÓN] Aquí findAll() SÍ es correcto: no filtramos en Java, solo transformamos
    // cada entidad en un DTO. El anti-patrón es findAll() + for/if para FILTRAR.
//    public List<IncidenciaResumenResponse> getIncidenciasResumen() {
//        return incidenciaRepository.findAll().stream()
//                .map(incidenciaRepository -> IncidenciaResumenResponse.builder()
//                        .nombreAlumno(incidenciaRepository.getAlumno().getNombre())
//                        .nombreCurso(incidenciaRepository.getCurso().getNombre())
//                        .idIncidencia(incidenciaRepository.getId())
//                        .build())
//                .toList();
//    }
}
