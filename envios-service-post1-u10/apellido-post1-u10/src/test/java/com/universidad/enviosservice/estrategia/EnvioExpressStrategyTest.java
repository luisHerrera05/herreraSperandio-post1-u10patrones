package com.universidad.enviosservice.estrategia;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EnvioExpressStrategyTest {

    private final EnvioExpressStrategy estrategia = new EnvioExpressStrategy();

    @ParameterizedTest(name = "peso={0} km={1} -> {2}")
    @CsvSource({
        "3.0,  100.0, 156.0",   // (120) * 1.3
        "5.0,  100.0, 156.0",   // limite exacto 5 kg
        "10.0, 100.0, 165.75",  // (120 + 7.5) * 1.3
        "20.0, 100.0, 185.25",  // (120 + 22.5) * 1.3 -> limite exacto 20 kg
        "25.0, 100.0, 198.25"   // (120 + 22.5 + 10) * 1.3
    })
    void calculaCostoPorTramoDePeso(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }

    @ParameterizedTest(name = "peso={0} km={1} -> {2}")
    @CsvSource({
        "3.0, 500.0, 780.0",    // limite exacto 500 km: sin recargo -> 600 * 1.3
        "3.0, 600.0, 968.5"     // > 500 km: (720 + 25) * 1.3
    })
    void aplicaRecargoSoloPorEncimaDe500Km(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }
}
