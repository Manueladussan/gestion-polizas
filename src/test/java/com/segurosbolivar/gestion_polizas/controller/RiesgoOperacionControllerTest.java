
package com.segurosbolivar.gestion_polizas.controller;

import com.segurosbolivar.gestion_polizas.entity.Riesgo;
import com.segurosbolivar.gestion_polizas.service.RiesgoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RiesgoOperacionController.class)
class RiesgoOperacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RiesgoService riesgoService;

    @Test
    void cancelarRiesgoDebeRetornarOk() throws Exception {
        Riesgo riesgo = new Riesgo();
        riesgo.setDescripcion("Vehículo asegurado");
        riesgo.setEstado("CANCELADO");

        when(riesgoService.cancelar(3L)).thenReturn(riesgo);

        mockMvc.perform(post("/riesgos/3/cancelar")
                        .header("x-api-key", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion")
                        .value("Vehículo asegurado"))
                .andExpect(jsonPath("$.estado").value("CANCELADO"));

        verify(riesgoService).cancelar(3L);
    }

    @Test
    void cancelarRiesgoSinApiKeyDebeRetornarUnauthorized()
            throws Exception {

        mockMvc.perform(post("/riesgos/3/cancelar"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(riesgoService);
    }
}
