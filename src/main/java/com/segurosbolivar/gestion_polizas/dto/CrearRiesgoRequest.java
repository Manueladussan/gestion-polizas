
package com.segurosbolivar.gestion_polizas.dto;

import jakarta.validation.constraints.NotBlank;

public class CrearRiesgoRequest {

    @NotBlank(message = "La descripción del riesgo es obligatoria")
    private String descripcion;

    public CrearRiesgoRequest() {
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
