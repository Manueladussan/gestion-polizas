package com.segurosbolivar.gestion_polizas.controller;

import com.segurosbolivar.gestion_polizas.entity.Poliza;
import com.segurosbolivar.gestion_polizas.exception.RecursoNoEncontradoException;
import com.segurosbolivar.gestion_polizas.service.PolizaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolizaController.class)
class PolizaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PolizaService polizaService;

    @Test
    void listarPolizasDebeRetornarOk() throws Exception {
        when(polizaService.listar(null, null))
                .thenReturn(List.of());

        mockMvc.perform(get("/polizas")
                        .header("x-api-key", "123456"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(polizaService).listar(null, null);
    }

    @Test
    void filtrarPorTipoYEstadoDebeEnviarParametrosAlServicio()
            throws Exception {

        when(polizaService.listar("COLECTIVA", "ACTIVA"))
                .thenReturn(List.of());

        mockMvc.perform(get("/polizas")
                        .param("tipo", "COLECTIVA")
                        .param("estado", "ACTIVA")
                        .header("x-api-key", "123456"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(polizaService).listar("COLECTIVA", "ACTIVA");
    }

    @Test
    void listarSinApiKeyDebeRetornarUnauthorized() throws Exception {
        mockMvc.perform(get("/polizas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void renovarDebeRetornarPolizaRenovada() throws Exception {
        Poliza poliza = new Poliza();
        poliza.setTipo("COLECTIVA");
        poliza.setEstado("RENOVADA");
        poliza.setCanon(new BigDecimal("1052000.00"));
        poliza.setPrima(new BigDecimal("210400.00"));
        poliza.setIpc(new BigDecimal("5.2"));

        when(polizaService.renovar(1L)).thenReturn(poliza);

        mockMvc.perform(post("/polizas/1/renovar")
                        .header("x-api-key", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RENOVADA"))
                .andExpect(jsonPath("$.canon").value(1052000.00))
                .andExpect(jsonPath("$.prima").value(210400.00));

        verify(polizaService).renovar(1L);
    }
    
    @Test
    void cancelarDebeRetornarPolizaCancelada() throws Exception {
        Poliza poliza = new Poliza();
        poliza.setEstado("CANCELADA");
        poliza.setTipo("COLECTIVA");
        poliza.setCanon(new BigDecimal("1000000.00"));
        poliza.setPrima(new BigDecimal("200000.00"));
        poliza.setIpc(new BigDecimal("5.2"));

        when(polizaService.cancelar(1L)).thenReturn(poliza);

        mockMvc.perform(post("/polizas/1/cancelar")
                        .header("x-api-key", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));

        verify(polizaService).cancelar(1L);
    }
    @Test
    void renovarPolizaInexistenteDebeRetornarNotFound() throws Exception {
        when(polizaService.renovar(999L))
                .thenThrow(new RecursoNoEncontradoException(
                        "No existe la póliza con id: 999"));

        mockMvc.perform(post("/polizas/999/renovar")
                        .header("x-api-key", "123456"))
                .andExpect(status().isNotFound());

        verify(polizaService).renovar(999L);
    }
    @Test
    void renovarPolizaCanceladaDebeRetornarBadRequest() throws Exception {
        when(polizaService.renovar(1L))
                .thenThrow(new RuntimeException(
                        "No se puede renovar una póliza cancelada"));

        mockMvc.perform(post("/polizas/1/renovar")
                        .header("x-api-key", "123456"))
                .andExpect(status().isBadRequest());

        verify(polizaService).renovar(1L);
    }
}
