package com.segurosbolivar.gestion_polizas.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/core-mock")
public class CoreMockController {

    private static final Logger logger =
            LoggerFactory.getLogger(CoreMockController.class);

    @PostMapping("/evento")
    public ResponseEntity<Map<String, String>> recibirEvento(
            @RequestBody Map<String, Object> evento) {

        logger.info("Intento de envío de evento a CORE: {}", evento);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Evento recibido por el CORE simulado"
        ));
    }
}
