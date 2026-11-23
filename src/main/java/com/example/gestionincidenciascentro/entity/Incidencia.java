package com.example.gestionincidenciascentro.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;

    private LocalDateTime fechaRegistro;  // fecha de creación de incidencia, se asigna automáticamente en @PrePersist

    // EnumType STRING: almacena el nombre del enum en la BD, no su ordinal (0, 1, 2...). Más legible y seguro.
    // EnumType ORDINAL: almacena el ordinal del enum en la BD (0, 1, 2...).
    // Si se cambia el orden de los valores del enum, los datos en la BD se corrompen
    @Enumerated(EnumType.STRING)
    private Estado estado;

    
    @ManyToOne(optional = false) // Una incidencia pertenece a un usuario (autor), no puede ser null (1,1)
//    @JoinColumn(name = "autor_id", nullable = false) // No es necesario, JPA genera automáticamente la columna "autor_id" en la tabla "incidencia"
    private Usuario autor;  // Usuario que ha creado la incidencia

    // Se ejecuta automáticamente antes de insertar una nueva incidencia
    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
    }


    public enum Estado {
        ABIERTA,
        EN_PROCESO,
        RESUELTA
    }

}


