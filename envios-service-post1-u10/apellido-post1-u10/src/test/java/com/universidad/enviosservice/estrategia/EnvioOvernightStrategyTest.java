package com.universidad.enviosservice.estrategia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EnvioOvernightStrategyTest {

    private final EnvioOvernightStrategy estrategia = new EnvioOvernightStrategy();

    @ParameterizedTest(name = "peso={0} km={1} -> {2}")
    @CsvSource({
        "3.0,  100.0, 220.0",   // peso <= 5: 200 + 20
        "5.0,  100.0, 220.0",   // limite exacto 5 kg
        "8.0,  100.0, 227.5",   // 5 < peso <= 20: 220 + 3*2.5
        "20.0, 100.0, 257.5",   // limite exacto 20 kg: 220 + 15*2.5
        "25.0, 100.0, 272.5"    // peso > 20: 220 + 37.5 + 5*3.0
    })
    void calculaCostoPorTramoDePeso(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }

    @Test
    void aceptaExactamente300Km() {
        assertEquals(620.0, estrategia.calcular(3.0, 300.0), 0.01);   // 600 + 20
    }

    @Test
    void rechazaDistanciasMayoresA300Km() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> estrategia.calcular(3.0, 301.0));
        assertEquals("OVERNIGHT no disponible para mas de 300 km", ex.getMessage());
    }
}
