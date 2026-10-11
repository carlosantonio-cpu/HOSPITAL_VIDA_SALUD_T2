package com.andinacapital.prestamo.controller;

import com.andinacapital.prestamo.config.RabbitConfig;
import com.andinacapital.prestamo.model.Cita;
import com.andinacapital.prestamo.model.CitaConfirmadaEvent;
import com.andinacapital.prestamo.repository.CitaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Locale;

@RestController
@RequestMapping("/citas")
public class CitaController {

    private final CitaRepository repository;
    private final AmqpTemplate amqpTemplate;
    private final ObjectMapper objectMapper;

    private final WebClient pacienteClient =
            WebClient.create("http://localhost:8081");

    public CitaController(
            CitaRepository repository,
            AmqpTemplate amqpTemplate,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.amqpTemplate = amqpTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public Flux<Cita> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Cita> buscar(@PathVariable Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Cita no encontrada"
                        )
                ));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Cita> crear(@RequestBody Cita cita) {

        if (cita.getPacienteId() == null
                || textoVacio(cita.getEspecialidad())
                || cita.getFechaHora() == null
                || textoVacio(cita.getProfesionalCodigo())) {

            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Paciente, especialidad, fecha y profesional son obligatorios"
            ));
        }

        if (cita.getFechaHora().isBefore(LocalDateTime.now())) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de la cita no puede estar en el pasado"
            ));
        }

        return validarPaciente(cita.getPacienteId())
                .then(Mono.defer(() -> {
                    cita.setId(null);
                    cita.setEstado("PENDIENTE");
                    cita.setFechaCreacion(LocalDateTime.now());
                    cita.setFechaActualizacion(LocalDateTime.now());

                    return repository.save(cita);
                }));
    }

    @PutMapping("/{id}/estado")
    public Mono<Cita> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {

        String nuevoEstado = estado.toUpperCase(Locale.ROOT);

        return repository.findById(id)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Cita no encontrada"
                        )
                ))
                .flatMap(cita -> {

                    String estadoActual = cita.getEstado();

                    boolean transicionPermitida =
                            ("PENDIENTE".equals(estadoActual)
                                    && ("CONFIRMADA".equals(nuevoEstado)
                                    || "CANCELADA".equals(nuevoEstado)))
                                    || ("CONFIRMADA".equals(estadoActual)
                                    && ("ATENDIDA".equals(nuevoEstado)
                                    || "CANCELADA".equals(nuevoEstado)
                                    || "AUSENTE".equals(nuevoEstado)));

                    if (!transicionPermitida) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Transición de estado no permitida: "
                                        + estadoActual + " -> " + nuevoEstado
                        ));
                    }

                    cita.setEstado(nuevoEstado);
                    cita.setFechaActualizacion(LocalDateTime.now());

                    return repository.save(cita)
                            .flatMap(guardada -> {
                                if ("CONFIRMADA".equals(nuevoEstado)) {
                                    return publicarEvento(guardada);
                                }
                                return Mono.just(guardada);
                            });
                });
    }

    private Mono<Void> validarPaciente(Long pacienteId) {
        return pacienteClient.get()
                .uri("/pacientes/{id}", pacienteId)
                .retrieve()
                .bodyToMono(Object.class)
                .then()
                .onErrorMap(
                        WebClientResponseException.NotFound.class,
                        error -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "El paciente indicado no existe"
                        )
                );
    }

    private Mono<Cita> publicarEvento(Cita cita) {
        CitaConfirmadaEvent evento = new CitaConfirmadaEvent(
                cita.getId(),
                cita.getPacienteId(),
                cita.getEspecialidad(),
                cita.getFechaHora(),
                cita.getEstado()
        );

        return Mono.fromCallable(
                        () -> objectMapper.writeValueAsString(evento))
                .flatMap(mensaje ->
                        Mono.fromRunnable(() ->
                                        amqpTemplate.convertAndSend(
                                                RabbitConfig.EXCHANGE,
                                                RabbitConfig.ROUTING_KEY,
                                                mensaje
                                        ))
                                .subscribeOn(Schedulers.boundedElastic())
                )
                .thenReturn(cita);
    }

    private boolean textoVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}