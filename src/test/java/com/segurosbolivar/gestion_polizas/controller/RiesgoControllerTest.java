package com.segurosbolivar.gestion_polizas.controller;

import com.segurosbolivar.gestion_polizas.entity.Riesgo;
import com.segurosbolivar.gestion_polizas.service.PolizaService;
import com.segurosbolivar.gestion_polizas.service.RiesgoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RiesgoController.class)
class RiesgoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PolizaService polizaService;

    @MockitoBean
    private RiesgoService riesgoService;

    @Test
    void listarRiesgosDebeRetornarOk() throws Exception {
        when(polizaService.buscarPorId(4L))
                .thenReturn(new com.segurosbolivar.gestion_polizas.entity.Poliza());
        when(riesgoService.listarPorPoliza(4L))
                .thenReturn(List.of());

        mockMvc.perform(get("/polizas/4/riesgos")
                        .header("x-api-key", "123456"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(riesgoService).listarPorPoliza(4L);
    }

    @Test
    void crearRiesgoDebeRetornarCreated() throws Exception {
        Riesgo riesgo = new Riesgo();
        riesgo.setDescripcion("Vehículo asegurado");
        riesgo.setEstado("ACTIVO");

        when(riesgoService.agregar(4L, "Vehículo asegurado"))
                .thenReturn(riesgo);

        mockMvc.perform(post("/polizas/4/riesgos")
                        .header("x-api-key", "123456")
                        .contentType("application/json")
                        .content("""
                                {
                                  "descripcion": "Vehículo asegurado"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descripcion")
                        .value("Vehículo asegurado"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));

        verify(riesgoService).agregar(4L, "Vehículo asegurado");
    }

    @Test
    void crearRiesgoConDescripcionVaciaDebeRetornarBadRequest()
            throws Exception {

        mockMvc.perform(post("/polizas/4/riesgos")
                        .header("x-api-key", "123456")
                        .contentType("application/json")
                        .content("""
                                {
                                  "descripcion": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value("La descripción del riesgo es obligatoria"));

        verifyNoInteractions(riesgoService);
    }

    @Test
    void consultarRiesgosSinApiKeyDebeRetornarUnauthorized()
            throws Exception {

        mockMvc.perform(get("/polizas/4/riesgos"))
                .andExpect(status().isUnauthorized());
    }
}
