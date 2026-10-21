package com.universidad.enviosservice.estrategia;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EnvioEstandarStrategyTest {

    private final EnvioEstandarStrategy estrategia = new EnvioEstandarStrategy();

    @ParameterizedTest(name = "peso={0} km={1} -> {2}")
    @CsvSource({
        "3.0,  100.0, 50.0",    // tramo peso <= 5
        "5.0,  100.0, 50.0",    // limite exacto 5 kg (sigue en el primer tramo)
        "10.0, 100.0, 54.0",    // 5 < peso <= 20: 50 + 5*0.8
        "20.0, 100.0, 62.0",    // limite exacto 20 kg: 50 + 15*0.8
        "25.0, 100.0, 68.0"     // peso > 20: 50 + 12 + 5*1.2
    })
    void calculaCostoPorTramoDePeso(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }

    @ParameterizedTest(name = "peso={0} km={1} -> {2}")
    @CsvSource({
        "3.0, 500.0, 250.0",    // limite exacto 500 km: SIN recargo
        "3.0, 600.0, 310.0"     // > 500 km: 300 + 10
    })
    void aplicaRecargoSoloPorEncimaDe500Km(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }
}
