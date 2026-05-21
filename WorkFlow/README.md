# 🛠️ Antigravity DevOps Framework (Agnostic Edition)

**Proyecto Actual:** Sudoku Java (Individual Improvement Test)  
**Versión:** 5.0 (Ultimate Enterprise Edition)

Este directorio conforma un ecosistema de automatización completo y de nivel profesional diseñado para asegurar el cumplimiento absoluto de los Resultados de Aprendizaje (RA) de Entornos de Desarrollo. No es solo un conjunto de scripts, es un **Framework DevOps Agnóstico** exportable a cualquier proyecto (Java, Node.js, Python), actuando como una barrera arquitectónica inquebrantable.

---

## 🌟 Arquitectura Enterprise Implementada (v5.0)

El proyecto ha sido dotado con la infraestructura más avanzada de la industria del software:

1. **Abstracción del Stack Tecnológico:** Configuración dinámica vía JSON. El ecosistema se adapta al lenguaje detectado (Maven, NPM, Pip) para linting, testing y versionado automático.
2. **Setup Wizard Universal:** Script de inicialización que instala y configura toda la arquitectura de seguridad y CI/CD en repositorios vacíos con un clic.
3. **Orquestador Central (CLI Interactiva):** Un único panel de control (`devops.ps1`) para operar todo el ciclo de vida del código sin necesidad de comandos complejos.
4. **Protector de Secretos Sensibles (Security Gate):** Escáner pre-commit que bloquea en local cualquier intento de subir contraseñas, URLs de bases de datos o tokens secretos.
5. **Análisis Estático de Código SAST (CodeQL):** Pipeline integrado con IA que bloquea Pull Requests si detecta vulnerabilidades, inyecciones de código o "Code Smells".
6. **Orquestación Multi-Entorno:** Despliegues paralelizados con barreras lógicas que separan `Staging` (Entorno QA - `develop`) de `Producción` (Entorno Final - `main`).
7. **Botón del Pánico (Disaster Recovery):** Sistema de Rollback de emergencia capaz de revertir Producción a la última versión estable en menos de 10 segundos en caso de fallos críticos.
8. **Mantenimiento Autónomo (Dependabot):** Actualización automática de dependencias. Si una librería externa se actualiza, el bot crea el PR, la nube valida los tests, y si todo pasa, la acción hace Merge automático sin humanos.
9. **ChatOps (Discord/Slack Webhooks):** Notificaciones en tiempo real al canal del equipo cada vez que se publica una nueva versión, enviando las Release Notes automáticamente.
10. **Automatización Semántica:** Enrutamiento inteligente de ramas (Smart PR Routing), auto-formato estricto (Pre-Push), generador automático de Changelogs analizando historial, y vinculación de Issues de GitHub.

---

## 📂 Inventario del Ecosistema

| Archivo / Componente | Propósito Principal |
| :--- | :--- |
| `devops.ps1` | **El Orquestador Central.** CLI Maestro para ejecutar todas las fases. |
| `WorkFlow/init_workflow.ps1` | Setup Wizard. Adapta el framework a nuevos repositorios. |
| `WorkFlow/09_Rollback_Emergencia.ps1` | El Botón del Pánico. Restaura producción a la versión anterior estable. |
| `WorkFlow/04_Commit_y_Push.ps1` | **El Gatekeeper Local.** Aplica SAST, formato, ejecuta tests locales y realiza commits. |
| `WorkFlow/08_Publicar_Release.ps1` | Gestor SemVer, generador de Changelog y gatillo del Webhook de ChatOps. |
| `WorkFlow/workflow_config.json` | El "Cerebro". Diccionario que define el lenguaje de programación, reglas de linting y secretos a bloquear. |
| `.github/workflows/pipeline.yml`| Pipeline CI/CD en la nube (SAST CodeQL, Pruebas Paralelas, Multi-Entorno). |
| `.github/dependabot.yml`| Configuración de actualización automatizada de dependencias. |

---

## 🚀 Guía de Uso Diario: El Flujo de Trabajo Industrial

Para trabajar como un Ingeniero de Software Sénior y garantizar tu calificación máxima, **nunca ejecutes comandos de Git manualmente**. 
**Simplemente ejecuta `.\devops.ps1` desde la raíz de tu proyecto** y sigue el menú:

### Fase 1: Inicio de Tarea
1. Abre el Menú Maestro y presiona `[1]` (Iniciar Tarea).
2. Selecciona si es una `feature`, `bugfix` o `hotfix`. El orquestador generará la rama bajo nomenclatura GitFlow.

### Fase 2: Desarrollo y Seguridad (El Bucle de Código)
1. Escribe tu código. Cuando estés listo, presiona `[2]` (Guardar Trabajo).
2. **Auditoría Local:** El sistema buscará contraseñas *hardcodeadas*. Si estás limpio, pedirá tu mensaje semántico.
3. **Validación:** Auto-formateará tu código y correrá la batería de Tests. Si fallas, el guardado se cancela.

### Fase 3: Integración en la Nube
1. Presiona `[3]` (Enviar a Revisión) para crear tu Pull Request.
2. La nube tomará el control: Ejecutará los **Tests Paralelos**, auditará tu código buscando vulnerabilidades (SAST CodeQL) y desplegará un informe QA en tu entorno de **Staging**.
3. Revisa y aprueba el PR en GitHub.

### Fase 4: Limpieza Automática
1. Presiona `[4]` (Limpiar Entorno). El orquestador borrará la basura local, las ramas antiguas y cerrará tu Issue en el Tablero de Proyectos de GitHub.

### Fase 5: Lanzamiento a Producción y ChatOps
1. Desde la rama `develop`, presiona `[5]` (Lanzar Producción).
2. Selecciona matemáticamente el incremento de versión (SemVer).
3. El sistema autogenerará el `CHANGELOG.md`, creará la Release en GitHub y enviará una **notificación rica a tu canal de Discord/Slack** informando al equipo del éxito.
4. GitHub Actions detectará el paso a `main` y desplegará tu aplicación en el Entorno Seguro de **Producción**.

### 🚨 Fase 6 (Opcional): Disaster Recovery
*   Si la versión recién publicada a Producción tiene un fallo crítico, presiona `[7]` (Rollback Emergencia). 
*   El orquestador revertirá el daño y devolverá tu sistema a la versión estable anterior en segundos.

---

*Desarrollado y arquitectado por Antigravity AI como solución de Calidad Enterprise.*
