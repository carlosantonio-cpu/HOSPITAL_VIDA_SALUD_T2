package com.andinacapital.prestamo.repository;

import com.andinacapital.prestamo.model.Desembolso;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface DesembolsoRepository
        extends ReactiveCrudRepository<Desembolso, Long> {
}