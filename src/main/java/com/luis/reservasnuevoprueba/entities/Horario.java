package com.luis.reservasnuevoprueba.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "horario")
public class Horario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;
    @Enumerated(EnumType.STRING)
    private DiaSemana diaSemana;
    private int sesionDia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    @JsonIgnore
    @ManyToMany(mappedBy = "horarios")
    private List<Reserva> reservas;
    public enum DiaSemana{
        LUNES,MARTES,MIERCOLES,JUEVES,VIERNES
    }
}
