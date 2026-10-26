package com.orientadorvocacional.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Agrega cabeceras HTTP de seguridad a todas las respuestas:
 * Content-Security-Policy (mitiga XSS: solo se ejecutan scripts propios),
 * proteccion contra clickjacking, MIME sniffing, fuga de Referer, y
 * HSTS cuando la aplicacion se sirve por HTTPS.
 */
@Component
public class FiltroCabecerasSeguridad extends OncePerRequestFilter {

    static final String POLITICA_CSP = String.join("; ",
            "default-src 'self'",
            "script-src 'self'",
            "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com",
            "font-src 'self' https://fonts.gstatic.com",
            "img-src 'self' data:",
            "connect-src 'self'",
            "object-src 'none'",
            "base-uri 'self'",
            "form-action 'self'",
            "frame-ancestors 'none'");

    private static final String HSTS_UN_ANIO = "max-age=31536000; includeSubDomains";
    private static final String CACHE_CATALOGO_CINCO_MINUTOS = "public, max-age=300";

    /** Catalogo publico (igual para todos): el navegador puede reutilizarlo unos minutos. */
    private static final Pattern CATALOGO_PUBLICO =
            Pattern.compile("^/api/(universidades|carreras(/[0-9]+(/pensum)?)?)$");

    @Override
    protected void doFilterInternal(HttpServletRequest peticion, HttpServletResponse respuesta,
                                    FilterChain cadena) throws ServletException, IOException {
        respuesta.setHeader("Content-Security-Policy", POLITICA_CSP);
        respuesta.setHeader("X-Content-Type-Options", "nosniff");
        respuesta.setHeader("X-Frame-Options", "DENY");
        respuesta.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        respuesta.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");
        respuesta.setHeader("Cross-Origin-Opener-Policy", "same-origin");

        if (peticion.isSecure()) {
            respuesta.setHeader("Strict-Transport-Security", HSTS_UN_ANIO);
        }

        String ruta = peticion.getRequestURI();
        if (ruta.startsWith("/api/")) {
            if ("GET".equals(peticion.getMethod()) && CATALOGO_PUBLICO.matcher(ruta).matches()) {
                respuesta.setHeader("Cache-Control", CACHE_CATALOGO_CINCO_MINUTOS);
            } else {
                respuesta.setHeader("Cache-Control", "no-store");
                respuesta.setHeader("Pragma", "no-cache");
            }
        }

        cadena.doFilter(peticion, respuesta);
    }
}