package com.luis.reservasnuevoprueba.service;


import com.luis.reservasnuevoprueba.entities.Aula;
import com.luis.reservasnuevoprueba.entities.Horario;
import com.luis.reservasnuevoprueba.entities.Reserva;
import com.luis.reservasnuevoprueba.exception.ReservaException;
import com.luis.reservasnuevoprueba.repository.AulaRepository;
import com.luis.reservasnuevoprueba.repository.ReservaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final AulaRepository aulaRepository;

    /**
     * Obtener todas las reservas
     */
    public List<Reserva> obtenerTodasLasReservas() {
        return reservaRepository.findAll();
    }

    /**
     * Obtener una reserva por ID
     */
    public Optional<Reserva> obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id);
    }

    /**
     * Crear una nueva reserva con validaciones
     */
    // Usa transaccional cuando vaya acceder a ubna referencia
    @Transactional
    public Reserva crearReserva(Reserva reserva) {
        // Validar que el aula existe
        if (reserva.getAula() == null || reserva.getAula().getId() == null) {
            throw new ReservaException("El aula es requerida");
        }

        Aula aula = aulaRepository.findById(reserva.getAula().getId())
                .orElseThrow(() -> new ReservaException("El aula no existe"));

        // Validar que no sea una fecha en el pasado
        validarFechaNoEsPasado(reserva.getFecha().toLocalDate());

        // Validar que el número de asistentes no supere la capacidad del aula
        validarCapacidadAula(reserva.getNumAsistentes(), aula.getCapacidad());

        // Validar que no haya solapamiento de reservas
        validarSolapamientoReservas(aula, reserva.getFecha().toLocalDate(), reserva.getHorarios());

        reserva.setAula(aula);
        return reservaRepository.save(reserva);
    }

    /**
     * Actualizar una reserva existente
     */
    public Reserva actualizarReserva(Long id, Reserva reservaActualizada) {
        Reserva reservaExistente = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaException("La reserva no existe"));

        // Validar que no sea una fecha en el pasado
        validarFechaNoEsPasado(reservaActualizada.getFecha().toLocalDate());

        // Validar capacidad
        Aula aula = reservaExistente.getAula();
        validarCapacidadAula(reservaActualizada.getNumAsistentes(), aula.getCapacidad());

        // Validar solapamiento solo si cambia la fecha u horarios
        if (!reservaExistente.getFecha().equals(reservaActualizada.getFecha()) ||
                !reservaExistente.getHorarios().equals(reservaActualizada.getHorarios())) {
            validarSolapamientoReservas(aula, reservaActualizada.getFecha().toLocalDate(),
                    reservaActualizada.getHorarios(), id);
        }

        reservaExistente.setFecha(reservaActualizada.getFecha());
        reservaExistente.setMotivo(reservaActualizada.getMotivo());
        reservaExistente.setNumAsistentes(reservaActualizada.getNumAsistentes());
        reservaExistente.setHorarios(reservaActualizada.getHorarios());

        return reservaRepository.save(reservaExistente);
    }

    /**
     * Eliminar una reserva
     */
    public void eliminarReserva(Long id, String emailUsuario) {
        Optional<Reserva> reserva = reservaRepository.findById(id);

        // Si no es ADMIN, solo puede borrar sus propias reservas
        if (!reserva.get().getUsuario().getEmail().equals(emailUsuario)) {
            throw new ReservaException("No puedes eliminar reservas de otros usuarios");
        }
    }

    /**
     * Obtener todas las reservas de un aula
     */
    public List<Reserva> obtenerReservasPorAula(Long aulaId) {
        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() -> new ReservaException("El aula no existe"));
        return reservaRepository.findByAula(aula);
    }

    /**
     * Obtener las reservas futuras de un aula
     */
    public List<Reserva> obtenerReservasFuturas(Long aulaId) {
        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() -> new ReservaException("El aula no existe"));
        return reservaRepository.findReservasFuturas(aula, LocalDate.now());
    }

    // ==================== VALIDACIONES ====================

    /**
     * Validar que la fecha no sea en el pasado
     */
    private void validarFechaNoEsPasado(LocalDate fecha) {
        if (fecha.isBefore(LocalDate.now())) {
            throw new ReservaException("No se pueden realizar reservas en fechas pasadas");
        }
    }

    /**
     * Validar que el número de asistentes no supere la capacidad del aula
     */
    private void validarCapacidadAula(Integer numAsistentes, Integer capacidad) {
        if (numAsistentes == null || numAsistentes <= 0) {
            throw new ReservaException("El número de asistentes debe ser mayor a 0");
        }
        if (numAsistentes > capacidad) {
            throw new ReservaException("El número de asistentes (" + numAsistentes +
                    ") supera la capacidad del aula (" + capacidad + ")");
        }
    }

    /**
     * Validar que no haya solapamiento de reservas (al crear)
     */
    private void validarSolapamientoReservas(Aula aula, LocalDate fecha, List<Horario> horarios) {
        List<Reserva> reservasConflicto = reservaRepository.findReservasQueSesolapan(aula, fecha, horarios);

        if (!reservasConflicto.isEmpty()) {
            throw new ReservaException("Ya existe una reserva en el aula para esa fecha y horario");
        }
    }

    /**
     * Validar que no haya solapamiento de reservas
     */
    private void validarSolapamientoReservas(Aula aula, LocalDate fecha, List<Horario> horarios, Long reservaActualId) {
        List<Reserva> reservasConflicto = reservaRepository.findReservasQueSesolapan(aula, fecha, horarios);

        /* Filtrar la reserva actual si existe en los resultados
        reservasConflicto.removeIf(r -> r.getId().equals(reservaActualId));
*/
        if (!reservasConflicto.isEmpty()) {
            throw new ReservaException("Ya existe una reserva en el aula para esa fecha y horario");
        }
    }
}