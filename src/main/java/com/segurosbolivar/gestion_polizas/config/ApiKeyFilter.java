
package com.segurosbolivar.gestion_polizas.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${api.key}")
    private String apiKeyEsperada;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String apiKeyRecibida = request.getHeader("x-api-key");

        if (apiKeyRecibida == null) {
            responderError(
                    response,
                    "La API Key es obligatoria");
            return;
        }

        if (!apiKeyEsperada.equals(apiKeyRecibida)) {
            responderError(
                    response,
                    "La API Key proporcionada es inválida");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void responderError(
            HttpServletResponse response,
            String mensaje) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"mensaje\":\"" + mensaje + "\"}");
    }
}
