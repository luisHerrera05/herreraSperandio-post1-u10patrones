package com.universidad.enviosservice.estrategia;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EnvioInternacionalStrategyTest {

    private final EnvioInternacionalStrategy estrategia = new EnvioInternacionalStrategy();

    @ParameterizedTest(name = "peso={0} km={1} -> {2}")
    @CsvSource({
        "3.0,  1000.0, 1850.0",  // peso <= 5: 1800 + 50
        "5.0,  1000.0, 1850.0",  // limite exacto 5 kg
        "10.0, 1000.0, 1865.0",  // 5 < peso <= 20: 1850 + 5*3.0
        "20.0, 1000.0, 1895.0",  // limite exacto 20 kg: 1850 + 15*3.0
        "25.0, 1000.0, 1917.5"   // peso > 20: 1850 + 45 + 5*4.5
    })
    void calculaCostoPorTramoDePeso(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }

    @ParameterizedTest(name = "peso={0} km={1} -> {2}")
    @CsvSource({
        "3.0, 5000.0,  9050.0",   // limite exacto 5000 km: SIN recargo
        "3.0, 6000.0, 10950.0"    // > 5000 km: 10800 + 50 + 100
    })
    void aplicaRecargoSoloPorEncimaDe5000Km(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }
}
