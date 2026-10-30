
package com.segurosbolivar.gestion_polizas.controller;

import com.segurosbolivar.gestion_polizas.dto.CrearRiesgoRequest;
import com.segurosbolivar.gestion_polizas.entity.Riesgo;
import com.segurosbolivar.gestion_polizas.service.PolizaService;
import com.segurosbolivar.gestion_polizas.service.RiesgoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polizas")
public class RiesgoController {

    private final PolizaService polizaService;
    private final RiesgoService riesgoService;

    public RiesgoController(PolizaService polizaService, RiesgoService riesgoService) {
        this.polizaService = polizaService;
        this.riesgoService = riesgoService;
    }

    @GetMapping("/{id}/riesgos")
    public List<Riesgo> listarPorPoliza(@PathVariable Long id) 
    {
        polizaService.buscarPorId(id);
        return riesgoService.listarPorPoliza(id);
    }

    @PostMapping("/{id}/riesgos")
    @ResponseStatus(HttpStatus.CREATED)
    public Riesgo agregar(@PathVariable Long id, @Valid @RequestBody CrearRiesgoRequest request) 
    {
        return riesgoService.agregar(id, request.getDescripcion());
    }
}
