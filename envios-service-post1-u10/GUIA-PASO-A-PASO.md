# Guía rápida: qué hacer TÚ (lo que no se puede automatizar)

## Contenido del zip
- `apellido-post1-u10/` → proyecto en su estado FINAL (Parte 2). Renombra `apellido` por tu apellido.
- `PARTE1-archivos/` → `ServicioCalculoEnvio.java` y `ServicioCalculoEnvioTest.java` ORIGINALES (estado Parte 1).

## Orden de trabajo (así tu historial de commits cuenta la historia real)

### PARTE 1
1. Levanta SonarQube (`docker run ...` de la guía) o SonarCloud; crea el proyecto `com.universidad:envios-service` y el token.
2. Copia el proyecto a una carpeta nueva y **reemplaza** por los archivos de `PARTE1-archivos/`:
   - `main/ServicioCalculoEnvio.java` → `src/main/java/com/universidad/enviosservice/service/`
   - `test/ServicioCalculoEnvioTest.java` → `src/test/java/com/universidad/enviosservice/service/`
   - **Borra** `src/main/.../estrategia/` y `src/test/.../estrategia/` (aún no existen en la Parte 1).
3. `mvn clean verify sonar:sonar -Dsonar.token=TU_TOKEN`
4. Abre el dashboard y `target/site/jacoco/index.html`; toma capturas → `docs/sonar-dashboard-inicial.png` y `docs/jacoco-inicial.png`.
   Anota: CC real, cobertura de líneas y de ramas, Code Smells, y qué ramas salen en rojo/amarillo en JaCoCo.
5. Commits y push (los de la guía):
   ```bash
   git init
   git add pom.xml .gitignore src/main/java/
   git commit -m "feat: agregar ServicioCalculoEnvio con calculo de costo por metodo de envio"
   git add src/test/java/ sonar-project.properties
   git commit -m "test: agregar suite inicial de ServicioCalculoEnvioTest y configurar SonarQube"
   git add README.md docs/
   git commit -m "docs: documentar hallazgos del analisis inicial de metricas"
   git branch -M main
   git remote add origin https://github.com/TU_USUARIO/TU_APELLIDO-post1-u10.git
   git push -u origin main
   git tag parte1 && git push origin parte1
   ```
   (En este punto el README solo necesita la sección de la Parte 1.)

### PARTE 2
6. Restaura los archivos finales del zip (estrategias, `ServicioCalculoEnvio` refactorizado y todas las pruebas).
7. Configura en SonarQube el Quality Profile "Estándar Laboratorio" (duplicar Sonar Way → regla S1541, Threshold 10 → asignar
   al proyecto) y el Quality Gate "Estándar Laboratorio" (Coverage < 70 %, New Code Smells > 0, Duplicated Lines > 5 %) y asígnalo.
   Toma captura de ambos (opcional pero suma).
8. `mvn clean verify sonar:sonar -Dsonar.token=TU_TOKEN` → capturas `docs/sonar-dashboard-final.png` y `docs/jacoco-final.png`.
9. Completa TODOS los `⚠️ COMPLETAR` del README con tus valores reales y el resultado del Quality Gate.
10. Commits y push:
   ```bash
   git add src/main/java/com/universidad/enviosservice/estrategia/ src/main/java/com/universidad/enviosservice/service/
   git commit -m "refactor: extraer calculo por metodo de envio a clases de estrategia"
   git add src/test/java/
   git commit -m "test: agregar pruebas de estrategias y casos limite antes no cubiertos"
   git add README.md docs/
   git commit -m "docs: completar README con quality gate, decisiones de diseño y metricas antes-despues"
   git push origin main
   ```
11. Verifica que el repo sea **público**, se llame `apellido-post1-u10` y sube el enlace al Aula virtual UDES.

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

