package com.andinacapital.prestamo.controller;

import com.andinacapital.prestamo.model.Cita;
import com.andinacapital.prestamo.repository.CitaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class CitaControllerTest {

    private CitaRepository repository;
    private CitaController controller;

    @BeforeEach
    void configurar() {
        repository = mock(CitaRepository.class);

        AmqpTemplate amqpTemplate = mock(AmqpTemplate.class);
        ObjectMapper objectMapper = new ObjectMapper();

        controller = new CitaController(
                repository,
                amqpTemplate,
                objectMapper
        );
    }

    @Test
    void rechazaTransicionInvalidaCon400() {
        Cita cita = new Cita();
        cita.setId(1L);
        cita.setEstado("CONFIRMADA");

        when(repository.findById(1L))
                .thenReturn(Mono.just(cita));

        StepVerifier.create(controller.cambiarEstado(1L, "PENDIENTE"))
                .expectErrorMatches(error ->
                        error instanceof ResponseStatusException ex
                                && ex.getStatusCode().value() == 400)
                .verify();

        verify(repository, never()).save(any(Cita.class));
    }

    @Test
    void rechazaCitaSinPacienteCon400() {
        Cita cita = new Cita();
        cita.setEspecialidad("Medicina General");

        StepVerifier.create(controller.crear(cita))
                .expectErrorMatches(error ->
                        error instanceof ResponseStatusException ex
                                && ex.getStatusCode().value() == 400)
                .verify();

        verifyNoInteractions(repository);
    }
}