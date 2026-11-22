package com.example.gestionincidenciascentro;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

}
