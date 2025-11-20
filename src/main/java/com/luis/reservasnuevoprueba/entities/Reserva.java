package com.luis.reservasnuevoprueba.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "reserva")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDateTime fecha;
    private String motivo;
    private Integer numAsistentes;
    @CreationTimestamp
    @Column
    private LocalDate fechaCreacion;
    @ManyToOne
    @JoinColumn(name = "aula_id")
    private Aula aula;
    @ManyToMany
    @JoinTable(
            name="reserva_horario",
            joinColumns = @JoinColumn(name = "reserva_id"),
            inverseJoinColumns = @JoinColumn(name = "horario_id")
    )
    private List<Horario>horarios;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    Usuario usuario;
}
