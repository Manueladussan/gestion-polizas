package com.segurosbolivar.gestion_polizas.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polizas")
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipo; // INDIVIDUAL o COLECTIVA

    @Column(nullable = false)
    private String estado; // ACTIVA, CANCELADA, RENOVADA

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal canon;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prima;

    @Column(nullable = false)
    private BigDecimal ipc;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @OneToMany(
        mappedBy = "poliza",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Riesgo> riesgos = new ArrayList<>();

    public Poliza() {
    }

    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getCanon() {
        return canon;
    }

    public void setCanon(BigDecimal canon) {
        this.canon = canon;
    }

    public BigDecimal getPrima() {
        return prima;
    }

    public void setPrima(BigDecimal prima) {
        this.prima = prima;
    }

    public BigDecimal getIpc() {
        return ipc;
    }

    public void setIpc(BigDecimal ipc) {
        this.ipc = ipc;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public List<Riesgo> getRiesgos() {
        return riesgos;
    }

    public void agregarRiesgo(Riesgo riesgo) {
        riesgos.add(riesgo);
        riesgo.setPoliza(this);
    }
}
