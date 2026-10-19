package com.orientadorvocacional.modelo;

import java.time.YearMonth;
import java.util.regex.Pattern;

/**
 * Datos de la tarjeta que escribe el usuario en la pasarela simulada.
 *
 * Estos datos solo viven en memoria durante el cobro: NUNCA se guardan
 * en la base de datos ni se escriben en los logs (por eso toString()
 * no muestra nada de la tarjeta).
 */
public class DatosTarjeta {

    private static final Pattern PATRON_VENCIMIENTO = Pattern.compile("^(0[1-9]|1[0-2])/(\\d{2})$");
    private static final Pattern PATRON_CVV = Pattern.compile("^\\d{3,4}$");
    private static final Pattern PATRON_NUMERO = Pattern.compile("^\\d{13,19}$");

    public static final int LONGITUD_MAXIMA_NOMBRE = 60;

    private String nombreTitular;
    private String numeroTarjeta;
    private String vencimiento;
    private String cvv;

    public DatosTarjeta(String nombreTitular, String numeroTarjeta, String vencimiento, String cvv) {
        setNombreTitular(nombreTitular);
        setNumeroTarjeta(numeroTarjeta);
        setVencimiento(vencimiento);
        setCvv(cvv);
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        if (nombreTitular == null || nombreTitular.isBlank()) {
            throw new IllegalArgumentException("Escribe el nombre que aparece en la tarjeta.");
        }
        String limpio = nombreTitular.trim();
        if (limpio.length() > LONGITUD_MAXIMA_NOMBRE) {
            throw new IllegalArgumentException("El nombre de la tarjeta es demasiado largo.");
        }
        this.nombreTitular = limpio;
    }

    /** Devuelve solo los digitos (sin espacios ni guiones). */
    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        if (numeroTarjeta == null) {
            throw new IllegalArgumentException("Escribe el número de la tarjeta.");
        }
        String soloDigitos = numeroTarjeta.replaceAll("[\\s-]", "");
        if (!PATRON_NUMERO.matcher(soloDigitos).matches()) {
            throw new IllegalArgumentException("El número de la tarjeta debe tener entre 13 y 19 dígitos.");
        }
        this.numeroTarjeta = soloDigitos;
    }

    public String getVencimiento() {
        return vencimiento;
    }

    /** Formato MM/AA. Rechaza fechas que ya pasaron. */
    public void setVencimiento(String vencimiento) {
        if (vencimiento == null || !PATRON_VENCIMIENTO.matcher(vencimiento.trim()).matches()) {
            throw new IllegalArgumentException("El vencimiento debe tener el formato MM/AA.");
        }
        String limpio = vencimiento.trim();
        int mes = Integer.parseInt(limpio.substring(0, 2));
        int anio = 2000 + Integer.parseInt(limpio.substring(3, 5));
        if (YearMonth.of(anio, mes).isBefore(YearMonth.now())) {
            throw new IllegalArgumentException("La tarjeta está vencida.");
        }
        this.vencimiento = limpio;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        if (cvv == null || !PATRON_CVV.matcher(cvv.trim()).matches()) {
            throw new IllegalArgumentException("El código de seguridad debe tener 3 o 4 dígitos.");
        }
        this.cvv = cvv.trim();
    }

    @Override
    public String toString() {
        return "DatosTarjeta[ocultos]";
    }
}
