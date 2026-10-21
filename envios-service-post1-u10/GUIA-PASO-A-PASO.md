
### PARTE 1
1. Levanta SonarQube (`docker run ...` de la guía) o SonarCloud; crea el proyecto `com.universidad:envios-service` y el token.
2. Copia el proyecto a una carpeta nueva y **reemplaza** por los archivos de `PARTE1-archivos/`:
   - `main/ServicioCalculoEnvio.java` → `src/main/java/com/universidad/enviosservice/service/`
   - `test/ServicioCalculoEnvioTest.java` → `src/test/java/com/universidad/enviosservice/service/`
   - **Borra** `src/main/.../estrategia/` y `src/test/.../estrategia/` (aún no existen en la Parte 1).
3. `mvn clean verify sonar:sonar -Dsonar.token=TU_TOKEN`
4. Abre el dashboard y `target/site/jacoco/index.html`; 
### PARTE 2
6. Restaura los archivos finales del zip (estrategias, `ServicioCalculoEnvio` refactorizado y todas las pruebas).
7. Configura en SonarQube el Quality Profile "Estándar Laboratorio" (duplicar Sonar Way → regla S1541, Threshold 10 → asignar
   al proyecto) y el Quality Gate "Estándar Laboratorio" (Coverage < 70 %, New Code Smells > 0, Duplicated Lines > 5 %) y asígnalo.


## Errores / imprecisiones que encontré en la guía (ya corregidos en el proyecto)
1. **Prueba de Overnight:** la guía espera `250.0` para 25 kg y 100 km. El valor correcto es **272.5**
   (200 + 20 + 15×2.5 + 5×3.0). Con 250 la prueba fallaría.
2. **`sonar-project.properties` no lo lee el plugin de Maven** (`mvn sonar:sonar`). Por eso las propiedades también están
   en el `pom.xml`. Si usas **SonarCloud**, cambia `sonar.host.url` a `https://sonarcloud.io` y agrega `sonar.organization`.
3. **Complejidad de las estrategias:** la guía dice 3–4; en realidad las cuatro quedan en **4** (el orquestador en 5).
   Reducción de `calcularCosto()`: 20 → 5 = 75 % (cumple el ≥ 50 %). Si SonarQube muestra otro número, usa el de TU dashboard.
4. La guía dice "Spring Initializr incluye .gitignore"; aquí es un proyecto Maven plano, así que se incluye uno propio.
5. El nombre/ubicación de la regla **S1541** puede variar según la versión de SonarQube (a veces aparece como "Methods should not be too complex"); búscala por la clave `java:S1541`.
6. Si el Quality Gate no pasa por **Coverage** global, mira qué clase baja el porcentaje; no lo "arregles" con pruebas vacías, documenta la condición que falla (la rúbrica lo acepta).

