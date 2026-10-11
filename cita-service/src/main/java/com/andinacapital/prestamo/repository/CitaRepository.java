package com.andinacapital.prestamo.repository;

import com.andinacapital.prestamo.model.Cita;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CitaRepository
        extends ReactiveCrudRepository<Cita, Long> {
}