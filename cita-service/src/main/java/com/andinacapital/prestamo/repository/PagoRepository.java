package com.andinacapital.prestamo.repository;

import com.andinacapital.prestamo.model.Pago;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PagoRepository
        extends ReactiveCrudRepository<Pago, Long> {
}