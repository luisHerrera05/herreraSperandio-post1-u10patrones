# Envios Service — Diagnóstico y Mejora Guiada por Métricas

> **⚠️ PLANTILLA:** todo lo marcado con `⚠️ COMPLETAR` debe reemplazarse con los datos REALES de TU dashboard.
> Los números no se inventan ni se copian de la guía. Borra este aviso al terminar.

Proyecto Maven (Java 21) que calcula el costo de un envío. Se analizó con SonarQube y JaCoCo, se refactorizó
con un patrón de diseño y se midió el efecto con evidencia real.

---

## Entorno de análisis

**⚠️ COMPLETAR: elige UNA opción y deja solo su texto.**

**Opción Docker local:** Elegí SonarQube Community en Docker por (1) *independencia de conectividad y de límites de un
plan gratuito*: el análisis corre aunque no tenga internet y no hay cuotas, y (2) *control total de la configuración*:
puedo crear Quality Profiles y Quality Gates propios sin restricciones. El costo asumido es que consume RAM/CPU de mi
equipo y debo mantener el contenedor corriendo durante todo el laboratorio.

**Opción SonarCloud:** Elegí SonarCloud por (1) *no requerir instalación*: me ahorra levantar Docker y su consumo de
recursos, y (2) *dashboard accesible desde cualquier equipo* con el mismo enlace, útil para compartir la evidencia.
El costo asumido es depender de internet y que, al ser repositorio público, el proyecto es visible para cualquiera.

## Estado inicial del análisis (Parte 1)

| Métrica | Valor real observado |
|---------|----------------------|
| Complejidad ciclomática de `calcularCosto()` | ⚠️ COMPLETAR (esperado: 20) |
| Cobertura de líneas de `ServicioCalculoEnvio` | ⚠️ COMPLETAR % |
| Cobertura de ramas de `ServicioCalculoEnvio` | ⚠️ COMPLETAR % |
| Code Smells reportados | ⚠️ COMPLETAR |

### Interpretación

`calcularCosto()` concentra el problema: es el único método con lógica de la clase y mezcla en un solo cuerpo la selección
del método de envío (4 ramas), los tramos de peso, los recargos por distancia y los ajustes por fragilidad y tipo de cliente.
Esto produce una complejidad ciclomática de 1 + 19 puntos de decisión = 20 (nivel "Alta" de la Tabla 3.2).
La cobertura de ramas es baja porque la suite inicial solo ejecuta dos caminos, ambos de `ESTANDAR` con 3 kg y 100 km.

Según el reporte HTML de JaCoCo, las ramas **sin cubrir** son: ⚠️ COMPLETAR mirando tu reporte (esperado: `EXPRESS`,
`OVERNIGHT`, `INTERNACIONAL` y el `else` de método no soportado; los tramos de peso 5–20 kg y >20 kg; los recargos de
distancia >500 km; `OVERNIGHT` con >300 km; `esFragil == true`; y el descuento `CORPORATIVO`).

La complejidad ciclomática alta y la cobertura baja **coinciden** en el mismo método: más caminos independientes
implican más casos de prueba necesarios y, como solo hay dos, queda sin ejercitar la mayor parte.

## Decisiones de diseño

### 1. Entorno de análisis
Ver sección "Entorno de análisis" (⚠️ COMPLETAR: referencia los dos criterios que usaste).

### 2. Exclusiones del análisis
No se excluyó nada (`sonar.exclusions` vacío). El proyecto no tiene clase `Main` ni código de infraestructura: todo el
código es lógica de negocio, así que excluirlo ocultaría justamente lo que se quiere medir. Si hubiera una clase de arranque
sin lógica, sí se excluiría para que su falta de pruebas no distorsione el porcentaje global.

### 3. Métrica usada como señal principal
**⚠️ COMPLETAR según tu dashboard.** Propuesta de redacción (ajústala a lo que veas): se usó la **Complejidad Ciclomática**
(Complexity) como señal principal porque cuantifica directamente el número de caminos independientes del método y, por
tanto, cuántas pruebas se necesitan; además, la cobertura baja es consecuencia de esa complejidad. La Complejidad
Cognitiva sirvió de apoyo (los `if` anidados de tramos de peso dentro de cada método de envío la elevan aún más) y la
cobertura confirmó el diagnóstico, pero por sí sola solo dice *qué falta probar*, no *por qué es difícil de probar*.

### 4. Patrón elegido: Strategy
| Candidato | ¿Aplica? | Por qué se descarta / elige |
|-----------|----------|-----------------------------|
| **Strategy** | **Sí** | Cada rama de `metodoEnvio` calcula el mismo tipo de resultado (un costo) con una fórmula propia, elegida una sola vez por un valor discreto de entrada, sin relación de orden entre ramas. |
| State | No | `ServicioCalculoEnvio` no guarda ningún estado entre llamadas ni existen transiciones válidas entre métodos de envío: cada invocación es independiente. |
| Chain of Responsibility | No | Los métodos de envío son mutuamente excluyentes: solo uno aplica y el orden de evaluación es irrelevante; no hay una solicitud que viaje por manejadores hasta que alguno la resuelva. |

**Qué se movió y qué se quedó.** A cada estrategia se movió únicamente la fórmula por tramos de peso y el recargo por
distancia propio de su método (y la excepción de distancia de `OVERNIGHT`). Los ajustes por **fragilidad** y **tipo de cliente**
se quedaron deliberadamente en el orquestador porque son reglas transversales a todos los métodos de envío (no son parte
de "qué fórmula usar"): llevarlas a las estrategias duplicaría esa lógica cuatro veces. Por eso el orquestador queda en CC = 5 y
no en 1.

Estructura resultante: `service/ServicioCalculoEnvio` (orquestador) y paquete `estrategia/` con la interfaz
`EstrategiaEnvio` y cuatro implementaciones.

### 5. Criterio de pruebas agregadas
Cada prueba nueva se corresponde con una rama que el reporte JaCoCo inicial mostraba sin cubrir:

| Rama sin cubrir (Parte 1) | Prueba que la cubre |
|---------------------------|---------------------|
| `EXPRESS`, `OVERNIGHT`, `INTERNACIONAL` | `enrutaCadaMetodoASuEstrategia` + una clase de prueba por estrategia |
| Método no soportado (`else`) | `rechazaMetodoDeEnvioNoSoportado` (verifica el mensaje) |
| Tramo 5 < peso ≤ 20 y peso > 20 (×4 métodos) | `calculaCostoPorTramoDePeso` en cada estrategia (valores 3, 5, 10/8, 20, 25 kg) |
| Recargo distancia > 500 km (Estándar, Express) | `aplicaRecargoSoloPorEncimaDe500Km` (500 km exacto vs. 600 km) |
| Recargo distancia > 5000 km (Internacional) | `aplicaRecargoSoloPorEncimaDe5000Km` (5000 vs. 6000 km) |
| `OVERNIGHT` con distancia > 300 km | `rechazaDistanciasMayoresA300Km` y `aceptaExactamente300Km` (límite) |
| `esFragil == true` | `aplicaRecargoPorFragilidad` |
| Descuento `CORPORATIVO` | `aplicaDescuentoClienteCorporativo` |
| Combinaciones fragilidad + descuento | `combinaFragilidadYDescuento*` |

Las pruebas verifican valores calculados a mano distintos entre sí y los **valores límite** (5 kg, 20 kg, 300 km, 500 km,
5000 km), que es donde suelen aparecer errores de `<` vs `<=`. No hay pruebas que solo invoquen el método sin aserción.
Antes de refactorizar se comprobó además que el comportamiento es idéntico al original (200.000 entradas aleatorias, 0 diferencias).

### 6. Realismo del Quality Gate
- **CC = 10 por método:** es el techo del rango "Moderada" de la Tabla 3.2 y el valor por defecto del perfil Sonar Way.
  Es exigente (el método original, con 20, habría fallado) sin obligar a reescribir métodos que aún son razonables de
  mantener. Con 5 sería demasiado estricto (incluso las estrategias, con CC = 4, quedarían al borde); con 20 habría dejado pasar el método problemático.
- **Cobertura ≥ 70 %:** 100 % es poco realista y empuja a escribir pruebas triviales solo para llenar el número
  (incentivo perverso, Sección 2.3). 70 % deja margen para código de infraestructura sin forzar pruebas vacías.
- **Duplicación ≤ 5 %** y **0 Code Smells nuevos** (esta última condición es la que hace efectiva la regla S1541, porque el
  Quality Gate no puede limitar la CC por método directamente: eso se configura en el Quality Profile).

**Resultado del Quality Gate:** ⚠️ COMPLETAR (Passed / Failed). Si falló: qué condición y por qué.

## Comparación antes / después

| Métrica | Antes (Parte 1) | Después (Parte 2) |
|---------|------------------|--------------------|
| Complejidad ciclomática de `calcularCosto()` | ⚠️ X (esperado 20) | ⚠️ X (esperado 5, −75 %) |
| Cobertura de ramas de la lógica de envío | ⚠️ X % | ⚠️ X % |
| Code Smells | ⚠️ X | ⚠️ X |
| Quality Gate | — | ⚠️ Passed / Failed |

## Capturas
![Dashboard inicial](docs/sonar-dashboard-inicial.png)
![JaCoCo inicial](docs/jacoco-inicial.png)
![Dashboard final](docs/sonar-dashboard-final.png)
![JaCoCo final](docs/jacoco-final.png)

## Cómo reproducir
```bash
mvn clean verify                                   # pruebas + reporte JaCoCo (target/site/jacoco/index.html)
mvn clean verify sonar:sonar -Dsonar.token=TOKEN   # análisis en SonarQube
```

## Herramientas utilizadas
- Java 21, Apache Maven 3.9, JUnit 5
- SonarQube Community Edition (o SonarCloud), JaCoCo, Docker (si aplica)
- Git, GitHub

## Conclusiones
⚠️ COMPLETAR con tus palabras (3–5 oraciones). Ideas: la complejidad ciclomática explica cuántas pruebas hacen falta;
Strategy redujo la CC del método de 20 a 5 al separar *qué fórmula usar* de *qué ajustes aplicar siempre*; la cobertura
solo es útil si las pruebas verifican valores límite y no solo si "pasan por" la línea; los umbrales de un Quality Gate
deben ser exigentes pero realistas.
