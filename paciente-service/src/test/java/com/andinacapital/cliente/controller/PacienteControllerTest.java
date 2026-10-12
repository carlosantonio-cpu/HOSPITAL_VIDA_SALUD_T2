package com.andinacapital.cliente.controller;

import com.andinacapital.cliente.model.Paciente;
import com.andinacapital.cliente.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class PacienteControllerTest {

    private PacienteRepository repository;
    private PacienteController controller;

    @BeforeEach
    void configurar() {
        repository = mock(PacienteRepository.class);
        controller = new PacienteController(repository);
    }

    @Test
    void rechazaPacienteDuplicadoCon409() {
        when(repository.findByTipoDocumentoAndNumeroDocumento(
                "DNI", "99999999"))
                .thenReturn(Mono.just(new Paciente()));

        Paciente paciente = new Paciente();
        paciente.setTipoDocumento("DNI");
        paciente.setNumeroDocumento("99999999");
        paciente.setNombres("Ana");
        paciente.setApellidos("Duplicada");

        StepVerifier.create(controller.registrar(paciente))
                .expectErrorMatches(error ->
                        error instanceof ResponseStatusException ex
                                && ex.getStatusCode().value() == 409)
                .verify();

        verify(repository, never()).save(any(Paciente.class));
    }

    @Test
    void rechazaPacienteSinNumeroDocumentoCon400() {
        Paciente paciente = new Paciente();
        paciente.setTipoDocumento("DNI");
        paciente.setNumeroDocumento(" ");
        paciente.setNombres("Ana");
        paciente.setApellidos("Prueba");

        StepVerifier.create(controller.registrar(paciente))
                .expectErrorMatches(error ->
                        error instanceof ResponseStatusException ex
                                && ex.getStatusCode().value() == 400)
                .verify();

        verifyNoInteractions(repository);
    }
}