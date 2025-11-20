package com.luis.reservasnuevoprueba.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    @Email(message = "El email no tiene el formato correcto")
    @NotBlank(message = "El email es obligatorio")
    @Column(unique = true)
    private String email;
    @NotBlank
    @Size(min = 3)
    private String password;
    private String roles; //"ROLE_USER,ROLE_ADMIN"

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
        return true;
    }

    //Devuelve el listado de roles
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convierte los roles de String a GrantedAuthority
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (String role : roles.split(",")) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @OneToMany
    private List<Reserva> reservas;
}
