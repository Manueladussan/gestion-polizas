
package com.segurosbolivar.gestion_polizas.controller;

import com.segurosbolivar.gestion_polizas.entity.Poliza;
import com.segurosbolivar.gestion_polizas.service.PolizaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polizas")
public class PolizaController {

    private final PolizaService polizaService;

    public PolizaController(PolizaService polizaService) {
        this.polizaService = polizaService;
    }

    @GetMapping
    public List<Poliza> listar(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado) {

        return polizaService.listar(tipo, estado);
    }
    
    @PostMapping("/{id}/renovar")
    public Poliza renovar(@PathVariable Long id) {
        return polizaService.renovar(id);
    }

    @PostMapping("/{id}/cancelar")
    public Poliza cancelar(@PathVariable Long id) {
        return polizaService.cancelar(id);
    }

}
