package com.example.gestionincidenciascentro.repository;

import com.example.gestionincidenciascentro.entity.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncidenciaRepository  extends JpaRepository<Incidencia, Long> {

    // SELECT ... WHERE autor.email = ?    el listado de un USER
    List<Incidencia> findByAutorEmail(String email);

    // SELECT ... WHERE id = ? AND autor.email = ?    una incidencia SOLO si es suya
    Optional<Incidencia> findByIdAndAutorEmail(Long id, String email);

}
