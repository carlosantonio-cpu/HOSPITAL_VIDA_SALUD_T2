package com.andinacapital.prestamo.repository;

import com.andinacapital.prestamo.model.Cuota;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface CuotaRepository
        extends ReactiveCrudRepository<Cuota, Long> {

    Flux<Cuota> findByPrestamoId(Long prestamoId);

    Flux<Cuota> findByFechaVencimientoBeforeAndEstadoNot(
            LocalDate fecha,
            String estado
    );
}