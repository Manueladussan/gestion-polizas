package com.segurosbolivar.gestion_polizas.service;

import com.segurosbolivar.gestion_polizas.entity.Poliza;
import com.segurosbolivar.gestion_polizas.exception.RecursoNoEncontradoException;
import com.segurosbolivar.gestion_polizas.repository.PolizaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import com.segurosbolivar.gestion_polizas.entity.Riesgo;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PolizaServiceTest {

    @Mock
    private PolizaRepository polizaRepository;

    private PolizaService polizaService;

    @BeforeEach
    void configurar() {
        polizaService = new PolizaService(polizaRepository);
    }

    @Test
    void renovarDebeAplicarIpcAlCanonYLaPrima() {
        Poliza poliza = crearPoliza("ACTIVA");

        when(polizaRepository.findById(1L))
                .thenReturn(Optional.of(poliza));
        when(polizaRepository.save(any(Poliza.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Poliza resultado = polizaService.renovar(1L);

        assertEquals(0, resultado.getCanon()
                .compareTo(new BigDecimal("1052000.00")));
        assertEquals(0, resultado.getPrima()
                .compareTo(new BigDecimal("210400.00")));
        assertEquals("RENOVADA", resultado.getEstado());

        verify(polizaRepository).save(poliza);
    }

    @Test
    void renovarPolizaCanceladaDebeLanzarExcepcion() {
        Poliza poliza = crearPoliza("CANCELADA");

        when(polizaRepository.findById(1L))
                .thenReturn(Optional.of(poliza));

        RuntimeException excepcion = assertThrows(
                RuntimeException.class,
                () -> polizaService.renovar(1L)
        );

        assertEquals(
                "No se puede renovar una póliza cancelada",
                excepcion.getMessage()
        );

        verify(polizaRepository, never()).save(any(Poliza.class));
    }

    @Test
    void buscarPolizaInexistenteDebeLanzarExcepcion() {
        when(polizaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> polizaService.buscarPorId(99L)
        );
    }

    
    @Test
    void cancelarPolizaInexistenteDebeLanzarExcepcion() {
        when(polizaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> polizaService.cancelar(99L)
        );

        verify(polizaRepository, never()).save(any(Poliza.class));
    }

    private Poliza crearPoliza(String estado) {
        Poliza poliza = new Poliza();
        poliza.setTipo("COLECTIVA");
        poliza.setEstado(estado);
        poliza.setCanon(new BigDecimal("1000000.00"));
        poliza.setPrima(new BigDecimal("200000.00"));
        poliza.setIpc(new BigDecimal("5.2"));
        return poliza;
    }

    
    @Test
    void cancelarPolizaDebeCancelarLaPolizaYTodosSusRiesgos() {
        Poliza poliza = crearPoliza("ACTIVA");

        Riesgo riesgo1 = new Riesgo();
        riesgo1.setDescripcion("Vehículo");
        riesgo1.setEstado("ACTIVO");

        Riesgo riesgo2 = new Riesgo();
        riesgo2.setDescripcion("Inmueble");
        riesgo2.setEstado("ACTIVO");

        poliza.getRiesgos().add(riesgo1);
        poliza.getRiesgos().add(riesgo2);

        when(polizaRepository.findById(1L))
                .thenReturn(Optional.of(poliza));
        when(polizaRepository.save(any(Poliza.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Poliza resultado = polizaService.cancelar(1L);

        assertEquals("CANCELADA", resultado.getEstado());
        assertEquals("CANCELADO", riesgo1.getEstado());
        assertEquals("CANCELADO", riesgo2.getEstado());

        verify(polizaRepository).save(poliza);
    }

}
