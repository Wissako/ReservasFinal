package com.luis.reservasnuevoprueba.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "aulas")
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(nullable = false)
    private Boolean esAulaDeOrdenadores;

    private Integer numeroOrdenadores;

    @JsonIgnore
    @OneToMany(mappedBy = "aula", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Reserva> reservas;
}