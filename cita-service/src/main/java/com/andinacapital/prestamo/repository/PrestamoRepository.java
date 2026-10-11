package com.andinacapital.prestamo.repository;

import com.andinacapital.prestamo.model.Prestamo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PrestamoRepository extends ReactiveCrudRepository<Prestamo, Long> {
}