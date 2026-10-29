package com.segurosbolivar.gestion_polizas.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CoreMockController.class)
class CoreMockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void recibirEventoDebeRetornarOk() throws Exception {
        mockMvc.perform(post("/core-mock/evento")
                        .header("x-api-key", "123456")
                        .contentType("application/json")
                        .content("""
                                {
                                  "tipoEvento": "POLIZA_RENOVADA",
                                  "polizaId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje")
                        .value("Evento recibido por el CORE simulado"));
    }

    @Test
    void recibirEventoSinApiKeyDebeRetornarUnauthorized()
            throws Exception {

        mockMvc.perform(post("/core-mock/evento")
                        .contentType("application/json")
                        .content("""
                                {
                                  "tipoEvento": "POLIZA_RENOVADA",
                                  "polizaId": 1
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }
}
