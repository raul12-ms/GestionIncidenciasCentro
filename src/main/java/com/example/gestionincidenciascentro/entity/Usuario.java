package com.example.gestionincidenciascentro.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
- implements UserDetails para que Spring Security pueda usar la entidad Usuario como usuario autenticado
 */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true)
    private String email;
    private String nombre;
    private String apellidos;
    @JsonIgnore // Nunca devolver el hash de la contraseña en una respuesta JSON
    private String password;
    private String roles;
    private LocalDate fechaNacimiento;
    private Boolean activo; // para controlar si el usuario está activo o no (p. ej. para bloquearlo sin borrarlo de la BD)

    private LocalDateTime fechaRegistro;  // fecha de creación del usuario, se asigna automáticamente en @PrePersist

    private LocalDateTime fechaUltimaModificacion;
    
    @JsonIgnore //Al convertir a JSON, no incluir la lista de incidencias para evitar bucles infinitos
    @OneToMany(mappedBy = "autor", cascade = CascadeType.REMOVE) // PERSIST
    private List<Incidencia> incidencias;

    // Se ejecuta automáticamente antes de insertar un nuevo usuario
    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
        this.fechaUltimaModificacion = LocalDateTime.now();
    }

    // Se ejecuta automáticamente antes de actualizar un usuario existente
    @PreUpdate
    protected void onUpdate() {

        this.fechaUltimaModificacion = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convierte los roles de String a GrantedAuthority para que Spring Security pueda usarlos en la autorización
        List<GrantedAuthority> authorities = new ArrayList<>();
        // Si roles es null (usuario insertado a mano en la BD) evitamos NullPointerException
        if (roles == null || roles.isBlank()) {
            return authorities;
        }
        //
        for (String role : roles.split(",")) {
            authorities.add(new SimpleGrantedAuthority(role.trim()));
        }

        return authorities;

    }

    // Estos metodos son necesarios para implementar UserDetails,
    // que es la interfaz que Spring Security usa para representar un usuario autenticado.
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // "activo" es Boolean (objeto). "return activo;" hace auto-unboxing y, si vale null
        // (p. ej. un usuario creado con un INSERT a mano), lanza NullPointerException en el login -> 500.
        // Boolean.TRUE.equals(activo) devuelve false si es null -> el login responde 401.
        return Boolean.TRUE.equals(activo);
    }
}
