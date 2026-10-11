package com.andinacapital.cliente.controller;

import com.andinacapital.cliente.model.Paciente;
import com.andinacapital.cliente.repository.PacienteRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteRepository repository;

    public PacienteController(PacienteRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Flux<Paciente> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Paciente> buscar(@PathVariable Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Paciente no encontrado"
                        )
                ));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Paciente> registrar(@RequestBody Paciente paciente) {

        if (textoVacio(paciente.getTipoDocumento())
                || textoVacio(paciente.getNumeroDocumento())
                || textoVacio(paciente.getNombres())
                || textoVacio(paciente.getApellidos())) {

            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de documento, número de documento, nombres y apellidos son obligatorios"
            ));
        }

        return repository.findByTipoDocumentoAndNumeroDocumento(
                        paciente.getTipoDocumento(),
                        paciente.getNumeroDocumento()
                )
                .hasElement()
                .flatMap(duplicado -> {

                    if (duplicado) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Ya existe un paciente con ese documento"
                        ));
                    }

                    paciente.setId(null);

                    if (paciente.getEstado() == null) {
                        paciente.setEstado("ACTIVO");
                    }

                    if (paciente.getFechaRegistro() == null) {
                        paciente.setFechaRegistro(LocalDateTime.now());
                    }

                    validarEstado(paciente.getEstado());

                    return repository.save(paciente);
                });
    }

    @PutMapping("/{id}")
    public Mono<Paciente> actualizar(
            @PathVariable Long id,
            @RequestBody Paciente datos
    ) {
        if (textoVacio(datos.getTipoDocumento())
                || textoVacio(datos.getNumeroDocumento())
                || textoVacio(datos.getNombres())
                || textoVacio(datos.getApellidos())) {

            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos obligatorios del paciente están incompletos"
            ));
        }

        return repository.findById(id)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Paciente no encontrado"
                        )
                ))
                .flatMap(actual -> repository
                        .findByTipoDocumentoAndNumeroDocumento(
                                datos.getTipoDocumento(),
                                datos.getNumeroDocumento()
                        )
                        .filter(otro -> !otro.getId().equals(id))
                        .hasElement()
                        .flatMap(duplicado -> {

                            if (duplicado) {
                                return Mono.error(
                                        new ResponseStatusException(
                                                HttpStatus.CONFLICT,
                                                "El documento ya pertenece a otro paciente"
                                        )
                                );
                            }

                            actual.setTipoDocumento(datos.getTipoDocumento());
                            actual.setNumeroDocumento(datos.getNumeroDocumento());
                            actual.setNombres(datos.getNombres());
                            actual.setApellidos(datos.getApellidos());
                            actual.setFechaNacimiento(datos.getFechaNacimiento());
                            actual.setTelefono(datos.getTelefono());
                            actual.setCorreo(datos.getCorreo());
                            actual.setDireccion(datos.getDireccion());

                            if (datos.getEstado() != null) {
                                validarEstado(datos.getEstado());
                                actual.setEstado(datos.getEstado());
                            }

                            return repository.save(actual);
                        }));
    }

    private boolean textoVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private void validarEstado(String estado) {
        if (!"ACTIVO".equals(estado) && !"INACTIVO".equals(estado)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estado debe ser ACTIVO o INACTIVO"
            );
        }
    }
}