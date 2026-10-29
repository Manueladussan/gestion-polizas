
package com.segurosbolivar.gestion_polizas.service;

import com.segurosbolivar.gestion_polizas.entity.Poliza;
import com.segurosbolivar.gestion_polizas.entity.Riesgo;
import com.segurosbolivar.gestion_polizas.repository.PolizaRepository;
import com.segurosbolivar.gestion_polizas.repository.RiesgoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.segurosbolivar.gestion_polizas.exception.RecursoNoEncontradoException;

import java.util.List;

@Service
public class RiesgoService {

    private final RiesgoRepository riesgoRepository;
    private final PolizaRepository polizaRepository;

    public RiesgoService(
            RiesgoRepository riesgoRepository,
            PolizaRepository polizaRepository) {
        this.riesgoRepository = riesgoRepository;
        this.polizaRepository = polizaRepository;
    }

    public List<Riesgo> listarPorPoliza(Long polizaId) {
        return riesgoRepository.findByPolizaId(polizaId);
    }

    @Transactional
    public Riesgo agregar(Long polizaId, String descripcion) {
        Poliza poliza = polizaRepository.findById(polizaId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe la póliza con id: " + polizaId));

        if (!"COLECTIVA".equalsIgnoreCase(poliza.getTipo())) {
            throw new RuntimeException(
                    "Solo se pueden agregar riesgos a pólizas colectivas");
        }

        if ("CANCELADA".equalsIgnoreCase(poliza.getEstado())) {
            throw new RuntimeException(
                    "No se pueden agregar riesgos a una póliza cancelada");
        }

        Riesgo riesgo = new Riesgo();
        riesgo.setDescripcion(descripcion);
        riesgo.setEstado("ACTIVO");

        poliza.agregarRiesgo(riesgo);

        return riesgoRepository.save(riesgo);
    }

    @Transactional
    public Riesgo cancelar(Long riesgoId) {
        Riesgo riesgo = riesgoRepository.findById(riesgoId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe el riesgo con id: " + riesgoId));

        riesgo.setEstado("CANCELADO");

        return riesgoRepository.save(riesgo);
    }
}
