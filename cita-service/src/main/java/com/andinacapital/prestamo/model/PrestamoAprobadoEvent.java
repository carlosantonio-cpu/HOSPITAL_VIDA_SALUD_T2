package com.andinacapital.prestamo.model;

import java.math.BigDecimal;

public class PrestamoAprobadoEvent {

    private Long prestamoId;
    private Long clienteId;
    private BigDecimal montoSolicitado;
    private BigDecimal montoMaximo;
    private String estado;

    public PrestamoAprobadoEvent() {
    }

    public PrestamoAprobadoEvent(
            Long prestamoId,
            Long clienteId,
            BigDecimal montoSolicitado,
            BigDecimal montoMaximo,
            String estado) {

        this.prestamoId = prestamoId;
        this.clienteId = clienteId;
        this.montoSolicitado = montoSolicitado;
        this.montoMaximo = montoMaximo;
        this.estado = estado;
    }

    public Long getPrestamoId() {
        return prestamoId;
    }

    public void setPrestamoId(Long prestamoId) {
        this.prestamoId = prestamoId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public BigDecimal getMontoSolicitado() {
        return montoSolicitado;
    }

    public void setMontoSolicitado(BigDecimal montoSolicitado) {
        this.montoSolicitado = montoSolicitado;
    }

    public BigDecimal getMontoMaximo() {
        return montoMaximo;
    }

    public void setMontoMaximo(BigDecimal montoMaximo) {
        this.montoMaximo = montoMaximo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}