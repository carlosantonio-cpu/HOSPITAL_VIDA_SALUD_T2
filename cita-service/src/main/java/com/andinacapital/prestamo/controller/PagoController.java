package com.andinacapital.prestamo.controller;

import com.andinacapital.prestamo.model.Cuota;
import com.andinacapital.prestamo.model.Pago;
import com.andinacapital.prestamo.repository.CuotaRepository;
import com.andinacapital.prestamo.repository.PagoRepository;

import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
public class PagoController {

    private final PagoRepository pagoRepository;
    private final CuotaRepository cuotaRepository;

    public PagoController(
            PagoRepository pagoRepository,
            CuotaRepository cuotaRepository) {

        this.pagoRepository = pagoRepository;
        this.cuotaRepository = cuotaRepository;
    }

    @GetMapping("/pagos")
    public Flux<Pago> listarPagos() {
        return pagoRepository.findAll();
    }

    @PostMapping("/cuotas/{cuotaId}/pago")
    public Mono<Pago> registrarPago(
            @PathVariable Long cuotaId,
            @RequestBody Pago solicitud) {

        return cuotaRepository
                .findById(cuotaId)

                .flatMap(cuota -> {

                    if (!"PENDIENTE".equals(
                            cuota.getEstado())) {

                        return Mono.error(
                                new IllegalStateException(
                                        "La cuota no está pendiente de pago."
                                )
                        );
                    }

                    Pago pago = new Pago();

                    pago.setCuotaId(cuota.getId());

                    pago.setFechaPago(
                            LocalDateTime.now()
                    );

                    pago.setMontoPagado(
                            solicitud.getMontoPagado()
                    );

                    pago.setMedioPago(
                            solicitud.getMedioPago()
                    );

                    pago.setNumeroOperacion(
                            solicitud.getNumeroOperacion()
                    );

                    pago.setEstado(
                            "REGISTRADO"
                    );

                    return pagoRepository
                            .save(pago)

                            .flatMap(guardado -> {

                                cuota.setEstado(
                                        "PAGADA"
                                );

                                cuota.setFechaPago(
                                        LocalDateTime.now()
                                                .toLocalDate()
                                );

                                return cuotaRepository
                                        .save(cuota)
                                        .thenReturn(guardado);
                            });
                });
    }
}