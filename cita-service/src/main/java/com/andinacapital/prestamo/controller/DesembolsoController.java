package com.andinacapital.prestamo.controller;

import com.andinacapital.prestamo.model.Desembolso;
import com.andinacapital.prestamo.model.Prestamo;
import com.andinacapital.prestamo.repository.DesembolsoRepository;
import com.andinacapital.prestamo.repository.PrestamoRepository;

import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
public class DesembolsoController {

    private final PrestamoRepository prestamoRepository;
    private final DesembolsoRepository desembolsoRepository;

    public DesembolsoController(
            PrestamoRepository prestamoRepository,
            DesembolsoRepository desembolsoRepository) {

        this.prestamoRepository = prestamoRepository;
        this.desembolsoRepository = desembolsoRepository;
    }

    @GetMapping("/desembolsos")
    public Flux<Desembolso> listarDesembolsos() {
        return desembolsoRepository.findAll();
    }

    @PostMapping("/prestamos/{prestamoId}/desembolso")
    public Mono<Desembolso> realizarDesembolso(
            @PathVariable Long prestamoId,
            @RequestBody Desembolso solicitud) {

        return prestamoRepository
                .findById(prestamoId)

                .flatMap(prestamo -> {

                    // Solo préstamos aprobados pueden desembolsarse
                    if (!"APROBADO".equals(prestamo.getEstado())) {
                        return Mono.error(
                                new IllegalStateException(
                                        "El préstamo no está aprobado para desembolso."
                                )
                        );
                    }

                    Desembolso desembolso = new Desembolso();

                    desembolso.setPrestamoId(prestamo.getId());

                    desembolso.setFechaDesembolso(
                            LocalDateTime.now()
                    );

                    desembolso.setMonto(
                            solicitud.getMonto() != null
                                    ? solicitud.getMonto()
                                    : prestamo.getMontoSolicitado()
                    );

                    desembolso.setMedioDesembolso(
                            solicitud.getMedioDesembolso()
                    );

                    desembolso.setEstado("REALIZADO");

                    return desembolsoRepository
                            .save(desembolso)

                            .flatMap(guardado -> {

                                prestamo.setEstado(
                                        "DESEMBOLSADO"
                                );

                                return prestamoRepository
                                        .save(prestamo)
                                        .thenReturn(guardado);
                            });
                });
    }
}