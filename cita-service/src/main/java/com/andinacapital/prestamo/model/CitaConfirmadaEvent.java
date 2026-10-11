package com.andinacapital.prestamo.model;

import java.time.LocalDateTime;

public record CitaConfirmadaEvent(
        Long citaId,
        Long pacienteId,
        String especialidad,
        LocalDateTime fechaHora,
        String estado
) {
}