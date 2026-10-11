package com.andinacapital.prestamo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("desembolsos")
public class Desembolso {

    @Id
    private Long id;

    @Column("prestamo_id")
    private Long prestamoId;

    @Column("fecha_desembolso")
    private LocalDateTime fechaDesembolso;

    private BigDecimal monto;

    @Column("medio_desembolso")
    private String medioDesembolso;

    private String estado;

    public Desembolso() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrestamoId() {
        return prestamoId;
    }

    public void setPrestamoId(Long prestamoId) {
        this.prestamoId = prestamoId;
    }

    public LocalDateTime getFechaDesembolso() {
        return fechaDesembolso;
    }

    public void setFechaDesembolso(LocalDateTime fechaDesembolso) {
        this.fechaDesembolso = fechaDesembolso;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMedioDesembolso() {
        return medioDesembolso;
    }

    public void setMedioDesembolso(String medioDesembolso) {
        this.medioDesembolso = medioDesembolso;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}