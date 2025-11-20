package com.luis.reservasnuevoprueba.service;


import com.luis.reservasnuevoprueba.entities.Aula;
import com.luis.reservasnuevoprueba.exception.AulaException;
import com.luis.reservasnuevoprueba.repository.AulaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AulaService {

    @Autowired
    private AulaRepository aulaRepository;

    /**
     * Obtener todas las aulas
     */
    public List<Aula> obtenerTodasLasAulas() {
        return aulaRepository.findAll();
    }

    /**
     * Obtener una aula por ID
     */
    public Optional<Aula> obtenerAulaPorId(Long id) {
        return aulaRepository.findById(id);
    }

    /**
     * Crear una nueva aula
     */
    public Aula crearAula(Aula aula) {
        validarAula(aula);
        return aulaRepository.save(aula);
    }

    /**
     * Actualizar un aula existente
     */
    public Aula actualizarAula(Long id, Aula aulaActualizada) {
        Aula aulaExistente = aulaRepository.findById(id)
                .orElseThrow(() -> new AulaException("El aula no existe"));

        validarAula(aulaActualizada);

        aulaExistente.setNombre(aulaActualizada.getNombre());
        aulaExistente.setCapacidad(aulaActualizada.getCapacidad());
        aulaExistente.setEsAulaDeOrdenadores(aulaActualizada.getEsAulaDeOrdenadores());

        // Solo actualizar numeroOrdenadores si es un aula de ordenadores
        if (aulaActualizada.getEsAulaDeOrdenadores()) {
            aulaExistente.setNumeroOrdenadores(aulaActualizada.getNumeroOrdenadores());
        } else {
            aulaExistente.setNumeroOrdenadores(null);
        }

        return aulaRepository.save(aulaExistente);
    }

    /**
     * Eliminar un aula
     */
    public void eliminarAula(Long id) {
        Aula aula = aulaRepository.findById(id)
                .orElseThrow(() -> new AulaException("El aula no existe"));
        aulaRepository.deleteById(id);
    }

    /**
     * Obtener aulas con capacidad mínima
     */
    public List<Aula> obtenerAulasPorCapacidad(Integer capacidad) {
        if (capacidad == null || capacidad <= 0) {
            throw new AulaException("La capacidad debe ser mayor a 0");
        }
        return aulaRepository.findByCapacidadMinima(capacidad);
    }

    /**
     * Obtener aulas con ordenadores
     */
    public List<Aula> obtenerAulasConOrdenadores() {
        return aulaRepository.findAulasConOrdenadores();
    }

    /**
     * Obtener aulas con ordenadores y capacidad mínima
     */
    public List<Aula> obtenerAulasConOrdenadoresYCapacidad(Integer capacidad) {
        if (capacidad == null || capacidad <= 0) {
            throw new AulaException("La capacidad debe ser mayor a 0");
        }
        return aulaRepository.findAulasConOrdenadoresYCapacidad(capacidad);
    }

    // ==================== VALIDACIONES ====================

    /**
     * Validar que un aula tenga los datos requeridos
     */
    private void validarAula(Aula aula) {
        if (aula.getNombre() == null || aula.getNombre().trim().isEmpty()) {
            throw new AulaException("El nombre del aula es requerido");
        }

        if (aula.getCapacidad() == null || aula.getCapacidad() <= 0) {
            throw new AulaException("La capacidad debe ser mayor a 0");
        }

        if (aula.getEsAulaDeOrdenadores() == null) {
            throw new AulaException("Debe indicar si es aula de ordenadores");
        }

        // Si es aula de ordenadores, validar que tenga número de ordenadores
        if (aula.getEsAulaDeOrdenadores() && (aula.getNumeroOrdenadores() == null || aula.getNumeroOrdenadores() <= 0)) {
            throw new AulaException("Las aulas de ordenadores deben tener al menos 1 ordenador");
        }

        // Si no es aula de ordenadores, el número de ordenadores debe ser nulo
        if (!aula.getEsAulaDeOrdenadores() && aula.getNumeroOrdenadores() != null) {
            throw new AulaException("Las aulas normales no pueden tener ordenadores");
        }
    }
}