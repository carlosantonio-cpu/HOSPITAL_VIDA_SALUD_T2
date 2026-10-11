package com.andinacapital.prestamo.controller;

import com.andinacapital.prestamo.config.RabbitConfig;
import com.andinacapital.prestamo.model.Prestamo;
import com.andinacapital.prestamo.model.PrestamoAprobadoEvent;
import com.andinacapital.prestamo.model.ResultadoEvaluacion;
import com.andinacapital.prestamo.model.SolicitudPrestamo;
import com.andinacapital.prestamo.repository.PrestamoRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class PrestamoController {

    private final PrestamoRepository prestamoRepository;

    private final AmqpTemplate amqpTemplate;

    private final ObjectMapper objectMapper;

    private final WebClient webClient =
            WebClient.create("http://localhost:8084");

    public PrestamoController(
            PrestamoRepository prestamoRepository,
            AmqpTemplate amqpTemplate,
            ObjectMapper objectMapper) {

        this.prestamoRepository = prestamoRepository;
        this.amqpTemplate = amqpTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/prestamos")
    public Flux<Prestamo> listarPrestamos() {
        return prestamoRepository.findAll();
    }

    @PostMapping("/prestamos")
    public Mono<Prestamo> solicitarPrestamo(
            @RequestBody SolicitudPrestamo solicitud) {

        /*
         * Comunicación síncrona:
         * prestamo-service solicita a evaluacion-service
         * que evalúe la solicitud.
         */
        return webClient
                .post()
                .uri("/evaluaciones")
                .bodyValue(solicitud)
                .retrieve()
                .bodyToMono(ResultadoEvaluacion.class)

                .flatMap(evaluacion -> {

                    String estado;

                    if ("APROBADA".equals(
                            evaluacion.getResultado())) {

                        estado = "APROBADO";

                    } else {

                        estado = "RECHAZADO";
                    }

                    Prestamo prestamo = new Prestamo();

                    prestamo.setClienteId(
                            evaluacion.getClienteId());

                    prestamo.setMontoSolicitado(
                            evaluacion.getMontoSolicitado());

                    prestamo.setIngresoMensual(
                            evaluacion.getIngresoMensual());

                    prestamo.setMontoMaximo(
                            evaluacion.getMontoMaximo());

                    prestamo.setNumeroCuotas(
                            solicitud.getNumeroCuotas()
                    );

                    prestamo.setEstado(estado);

                    return prestamoRepository
                            .save(prestamo)

                            .flatMap(guardado -> {

                                /*
                                 * Solo un préstamo aprobado
                                 * genera un evento asíncrono.
                                 */
                                if ("APROBADO".equals(
                                        guardado.getEstado())) {

                                    PrestamoAprobadoEvent evento =
                                            new PrestamoAprobadoEvent(
                                                    guardado.getId(),
                                                    guardado.getClienteId(),
                                                    guardado.getMontoSolicitado(),
                                                    guardado.getMontoMaximo(),
                                                    guardado.getEstado()
                                            );

                                    try {

                                        String mensaje =
                                                objectMapper
                                                        .writeValueAsString(
                                                                evento
                                                        );

                                        amqpTemplate.convertAndSend(
                                                RabbitConfig.EXCHANGE,
                                                RabbitConfig.ROUTING_KEY,
                                                mensaje
                                        );

                                    } catch (
                                            JsonProcessingException e) {

                                        return Mono.error(e);
                                    }
                                }

                                return Mono.just(guardado);
                            });
                });
    }
}