
package com.segurosbolivar.gestion_polizas.service;

import com.segurosbolivar.gestion_polizas.entity.Poliza;
import com.segurosbolivar.gestion_polizas.entity.Riesgo;
import com.segurosbolivar.gestion_polizas.exception.RecursoNoEncontradoException;
import com.segurosbolivar.gestion_polizas.repository.PolizaRepository;
import com.segurosbolivar.gestion_polizas.repository.RiesgoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiesgoServiceTest {

    @Mock
    private RiesgoRepository riesgoRepository;

    @Mock
    private PolizaRepository polizaRepository;

    private RiesgoService riesgoService;

    @BeforeEach
    void configurar() {
        riesgoService = new RiesgoService(
                riesgoRepository,
                polizaRepository
        );
    }

    @Test
    void agregarRiesgoAPolizaColectivaDebeCrearRiesgoActivo() {
        Poliza poliza = crearPoliza("COLECTIVA", "ACTIVA");

        when(polizaRepository.findById(1L))
                .thenReturn(Optional.of(poliza));
        when(riesgoRepository.save(any(Riesgo.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Riesgo resultado = riesgoService.agregar(
                1L,
                "Vehículo asegurado"
        );

        assertEquals("Vehículo asegurado", resultado.getDescripcion());
        assertEquals("ACTIVO", resultado.getEstado());
        assertSame(poliza, resultado.getPoliza());

        verify(riesgoRepository).save(resultado);
    }

    @Test
    void agregarRiesgoAPolizaIndividualDebeLanzarExcepcion() {
        Poliza poliza = crearPoliza("INDIVIDUAL", "ACTIVA");

        when(polizaRepository.findById(2L))
                .thenReturn(Optional.of(poliza));

        RuntimeException excepcion = assertThrows(
                RuntimeException.class,
                () -> riesgoService.agregar(2L, "Vehículo asegurado")
        );

        assertEquals(
                "Solo se pueden agregar riesgos a pólizas colectivas",
                excepcion.getMessage()
        );

        verify(riesgoRepository, never()).save(any(Riesgo.class));
    }

    @Test
    void agregarRiesgoAPolizaCanceladaDebeLanzarExcepcion() {
        Poliza poliza = crearPoliza("COLECTIVA", "CANCELADA");

        when(polizaRepository.findById(3L))
                .thenReturn(Optional.of(poliza));

        RuntimeException excepcion = assertThrows(
                RuntimeException.class,
                () -> riesgoService.agregar(3L, "Vehículo asegurado")
        );

        assertEquals(
                "No se pueden agregar riesgos a una póliza cancelada",
                excepcion.getMessage()
        );

        verify(riesgoRepository, never()).save(any(Riesgo.class));
    }

    @Test
    void agregarRiesgoAPolizaInexistenteDebeLanzarExcepcion() {
        when(polizaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> riesgoService.agregar(99L, "Vehículo asegurado")
        );

        verify(riesgoRepository, never()).save(any(Riesgo.class));
    }

    @Test
    void cancelarRiesgoDebeCambiarSuEstado() {
        Riesgo riesgo = new Riesgo();
        riesgo.setDescripcion("Vehículo asegurado");
        riesgo.setEstado("ACTIVO");

        when(riesgoRepository.findById(1L))
                .thenReturn(Optional.of(riesgo));
        when(riesgoRepository.save(any(Riesgo.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Riesgo resultado = riesgoService.cancelar(1L);

        assertEquals("CANCELADO", resultado.getEstado());
        verify(riesgoRepository).save(riesgo);
    }

    @Test
    void cancelarRiesgoInexistenteDebeLanzarExcepcion() {
        when(riesgoRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> riesgoService.cancelar(99L)
        );
    }

    private Poliza crearPoliza(String tipo, String estado) {
        Poliza poliza = new Poliza();
        poliza.setTipo(tipo);
        poliza.setEstado(estado);
        poliza.setCanon(new BigDecimal("1000000.00"));
        poliza.setPrima(new BigDecimal("200000.00"));
        poliza.setIpc(new BigDecimal("5.2"));
        return poliza;
    }
}
