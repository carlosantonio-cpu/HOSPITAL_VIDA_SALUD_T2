package com.andinacapital.prestamo.controller;

import com.andinacapital.prestamo.model.Cuota;
import com.andinacapital.prestamo.model.Prestamo;
import com.andinacapital.prestamo.repository.CuotaRepository;
import com.andinacapital.prestamo.repository.PrestamoRepository;

import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CuotaController {

    private final CuotaRepository cuotaRepository;
    private final PrestamoRepository prestamoRepository;

    public CuotaController(
            CuotaRepository cuotaRepository,
            PrestamoRepository prestamoRepository) {

        this.cuotaRepository = cuotaRepository;
        this.prestamoRepository = prestamoRepository;
    }

    @GetMapping("/cuotas")
    public Flux<Cuota> listarCuotas() {
        return cuotaRepository.findAll();
    }

    @GetMapping("/prestamos/{prestamoId}/cuotas")
    public Flux<Cuota> listarCuotasPorPrestamo(
            @PathVariable Long prestamoId) {

        return cuotaRepository.findByPrestamoId(prestamoId);
    }

    @GetMapping("/cuotas/morosas")
    public Flux<Cuota> listarCuotasMorosas() {

        return cuotaRepository
                .findByFechaVencimientoBeforeAndEstadoNot(
                        LocalDate.now(),
                        "PAGADA"
                );
    }

    @PostMapping("/prestamos/{prestamoId}/cuotas/generar")
    public Flux<Cuota> generarCuotas(
            @PathVariable Long prestamoId) {

        /*
         * Primero verificamos si el préstamo
         * ya tiene cuotas generadas.
         */
        return cuotaRepository
                .findByPrestamoId(prestamoId)
                .hasElements()

                .flatMapMany(yaExisten -> {

                    if (yaExisten) {

                        return Flux.error(
                                new IllegalStateException(
                                        "El préstamo ya tiene cuotas generadas."
                                )
                        );
                    }

                    /*
                     * Si no existen cuotas, buscamos
                     * el préstamo.
                     */
                    return prestamoRepository
                            .findById(prestamoId)

                            .flatMapMany(prestamo -> {

                                // El préstamo debe estar desembolsado
                                if (!"DESEMBOLSADO".equals(
                                        prestamo.getEstado())) {

                                    return Flux.error(
                                            new IllegalStateException(
                                                    "El préstamo debe estar desembolsado."
                                            )
                                    );
                                }

                                Integer numeroCuotas =
                                        prestamo.getNumeroCuotas();

                                if (numeroCuotas == null
                                        || numeroCuotas <= 0) {

                                    return Flux.error(
                                            new IllegalStateException(
                                                    "El préstamo no tiene un número de cuotas válido."
                                            )
                                    );
                                }

                                /*
                                 * Calculamos la cuota base
                                 * con dos decimales.
                                 */
                                BigDecimal montoBase =
                                        prestamo.getMontoSolicitado()
                                                .divide(
                                                        BigDecimal.valueOf(
                                                                numeroCuotas
                                                        ),
                                                        2,
                                                        RoundingMode.DOWN
                                                );

                                List<Cuota> cuotas =
                                        new ArrayList<>();

                                BigDecimal totalAcumulado =
                                        BigDecimal.ZERO;

                                for (int i = 1;
                                     i <= numeroCuotas;
                                     i++) {

                                    Cuota cuota =
                                            new Cuota();

                                    cuota.setPrestamoId(
                                            prestamo.getId()
                                    );

                                    cuota.setNumeroCuota(i);

                                    cuota.setFechaVencimiento(
                                            LocalDate.now()
                                                    .plusMonths(i)
                                    );

                                    BigDecimal montoCuota;

                                    /*
                                     * La última cuota ajusta
                                     * cualquier diferencia
                                     * por redondeo.
                                     */
                                    if (i == numeroCuotas) {

                                        montoCuota =
                                                prestamo
                                                        .getMontoSolicitado()
                                                        .subtract(
                                                                totalAcumulado
                                                        );

                                    } else {

                                        montoCuota =
                                                montoBase;
                                    }

                                    cuota.setMonto(
                                            montoCuota
                                    );

                                    cuota.setEstado(
                                            "PENDIENTE"
                                    );

                                    cuota.setFechaPago(
                                            null
                                    );

                                    totalAcumulado =
                                            totalAcumulado.add(
                                                    montoCuota
                                            );

                                    cuotas.add(cuota);
                                }

                                return cuotaRepository
                                        .saveAll(cuotas);
                            });
                });
    }
}