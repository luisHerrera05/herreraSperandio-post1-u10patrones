package com.universidad.enviosservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ServicioCalculoEnvioTest {

    private final ServicioCalculoEnvio servicio = new ServicioCalculoEnvio();

    // ---- Suite inicial (Parte 1) ----
    @Test
    void calculaEnvioEstandarPesoBajo() {
        double costo = servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "REGULAR", false);
        assertEquals(50.0, costo, 0.01);
    }

    @Test
    void aplicaDescuentoClientePremium() {
        double costo = servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "PREMIUM", false);
        assertEquals(45.0, costo, 0.01);
    }

    // ---- Ramas nuevas del orquestador (Parte 2) ----
    @Test
    void aplicaRecargoPorFragilidad() {
        // 50 + 15% = 57.5
        assertEquals(57.5, servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "REGULAR", true), 0.01);
    }

    @Test
    void aplicaDescuentoClienteCorporativo() {
        // 50 * 0.85 = 42.5
        assertEquals(42.5, servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "CORPORATIVO", false), 0.01);
    }

    @Test
    void combinaFragilidadYDescuentoCorporativo() {
        // 50 * 1.15 * 0.85 = 48.875
        assertEquals(48.875, servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "CORPORATIVO", true), 0.001);
    }

    @Test
    void combinaFragilidadYDescuentoPremium() {
        // 50 * 1.15 * 0.9 = 51.75
        assertEquals(51.75, servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "PREMIUM", true), 0.001);
    }

    @Test
    void rechazaMetodoDeEnvioNoSoportado() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> servicio.calcularCosto("DRON", 3.0, 100.0, "REGULAR", false));
        assertEquals("Metodo de envio no soportado: DRON", ex.getMessage());
    }

    // ---- Integracion: el orquestador enruta a la estrategia correcta ----
    @ParameterizedTest
    @CsvSource({
        "ESTANDAR,      3.0, 100.0,  50.0",
        "EXPRESS,       3.0, 100.0, 156.0",
        "OVERNIGHT,     3.0, 100.0, 220.0",
        "INTERNACIONAL, 3.0, 100.0, 230.0"
    })
    void enrutaCadaMetodoASuEstrategia(String metodo, double peso, double km, double esperado) {
        assertEquals(esperado, servicio.calcularCosto(metodo, peso, km, "REGULAR", false), 0.01);
    }

    @Test
    void propagaExcepcionDeOvernightFueraDeCobertura() {
        assertThrows(IllegalArgumentException.class,
            () -> servicio.calcularCosto("OVERNIGHT", 3.0, 301.0, "REGULAR", false));
    }
}
