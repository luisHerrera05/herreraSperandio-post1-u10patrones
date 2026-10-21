package com.universidad.enviosservice.estrategia;

/**
 * Contrato comun de los algoritmos de calculo de costo base por metodo de envio.
 * No incluye recargos transversales (fragilidad, tipo de cliente): esos viven en el orquestador.
 */
public interface EstrategiaEnvio {
    double calcular(double pesoKg, double distanciaKm);
}
