package com.segurosbolivar.gestion_polizas.service;

import com.segurosbolivar.gestion_polizas.entity.Poliza;
import com.segurosbolivar.gestion_polizas.entity.Riesgo;
import com.segurosbolivar.gestion_polizas.exception.RecursoNoEncontradoException;
import com.segurosbolivar.gestion_polizas.repository.PolizaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PolizaService {

    private final PolizaRepository polizaRepository;

    public PolizaService(PolizaRepository polizaRepository) {
        this.polizaRepository = polizaRepository;
    }

    public List<Poliza> listar(String tipo, String estado) {
        if (tipo != null && estado != null) {
            return polizaRepository.findByTipoAndEstado(tipo, estado);
        }

        if (tipo != null) {
            return polizaRepository.findByTipo(tipo);
        }

        if (estado != null) {
            return polizaRepository.findByEstado(estado);
        }

        return polizaRepository.findAll();
    }

    public Poliza buscarPorId(Long id) {
        return polizaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe la póliza con id: " + id));
    }

    @Transactional
    public Poliza renovar(Long id) {
        Poliza poliza = buscarPorId(id);

        if ("CANCELADA".equalsIgnoreCase(poliza.getEstado())) {
            throw new RuntimeException(
                    "No se puede renovar una póliza cancelada");
        }

        BigDecimal factor = BigDecimal.ONE.add(
                poliza.getIpc().divide(new BigDecimal("100"))
        );

        poliza.setCanon(poliza.getCanon().multiply(factor));
        poliza.setPrima(poliza.getPrima().multiply(factor));
        poliza.setEstado("RENOVADA");

        return polizaRepository.save(poliza);
    }

    @Transactional
    public Poliza cancelar(Long id) {
        Poliza poliza = buscarPorId(id);

        poliza.setEstado("CANCELADA");

        for (Riesgo riesgo : poliza.getRiesgos()) {
            riesgo.setEstado("CANCELADO");
        }

        return polizaRepository.save(poliza);
    }
}
