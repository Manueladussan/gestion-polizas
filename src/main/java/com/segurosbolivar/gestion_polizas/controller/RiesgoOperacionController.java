
package com.segurosbolivar.gestion_polizas.controller;

import com.segurosbolivar.gestion_polizas.entity.Riesgo;
import com.segurosbolivar.gestion_polizas.service.RiesgoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/riesgos")
public class RiesgoOperacionController {

    private final RiesgoService riesgoService;

    public RiesgoOperacionController(RiesgoService riesgoService) {
        this.riesgoService = riesgoService;
    }

    @PostMapping("/{id}/cancelar")
    public Riesgo cancelar(@PathVariable Long id) 
    {
        return riesgoService.cancelar(id);
    }
}
