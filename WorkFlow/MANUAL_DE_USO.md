# 📘 Manual de Uso: WorkFlow Sudoku Elite (v3.0)

Este manual explica cómo utilizar la suite de automatización para cumplir con la rúbrica de **Entornos de Desarrollo**. La suite es multiplataforma y soporta **Windows (PowerShell)** y **Linux/macOS (Bash)**.

> [!NOTE]
> Para usar la versión Bash (`.sh`), asegúrate de tener instalado `jq` para procesar archivos JSON.

---

## 🛠️ Herramientas Locales (PowerShell)

### 1. Gestión de Ramas (`03_Nueva_Rama.ps1`)
*   **Uso**: Crea ramas siguiendo GitFlow estricto.
*   **Prompt para la IA**: *"Necesito desarrollar la lógica de validación de filas en Java. Por favor, usa el script de Nueva Rama para crear una feature llamada `sudoku-row-validation` asegurándote de que parta de `develop`."*

### 2. Control de Calidad (`02_Quality_QA/Run_Tests.ps1`)
*   **Uso**: Ejecuta pruebas unitarias y valida el Quality Gate (80% cobertura).
*   **Prompt para la IA**: *"Ejecuta el script de QA y confírmame si pasamos el Quality Gate del 80%."*

### 3. Ambiente de Mocking (`02_Quality_QA/Mock_Database.ps1`)
*   **Uso**: Levanta un contenedor MySQL en Docker para tests realistas sin ensuciar tu DB local.
*   **Prompt para la IA**: *"Necesito probar la persistencia del Sudoku. Levanta el ambiente de Mocking con Docker y configura el proyecto para usar el puerto 3307."*

### 4. Sincronización de Proyecto (`02_Sincronizar_Todo.ps1`)
*   **Uso**: Vincula todos los Issues abiertos al tablero de GitHub Project V2.
*   **Prompt para la IA**: *"Sincroniza el tablero del proyecto para reflejar los últimos Issues creados relacionados con la documentación UML."*

### 5. Modo Escudo (Git Hooks - `01_Repositorio/Install_Hooks.ps1`)
*   **Uso**: Activa la protección automática de tu repositorio.
*   **¿Qué hace?**: Instala vigilantes en tu carpeta `.git` que bloquean cualquier `git commit` si los tests fallan o si el mensaje no es en inglés técnico. Es la "seguridad pasiva" definitiva.
*   **Prompt para la IA**: *"Activa el Modo Escudo instalando los Git Hooks para asegurar que todo commit pase el Quality Gate automáticamente."*

---

## 🤖 Prompts Expertos para Mantenimiento Continuo

Copia y pega estos prompts para que tu asistente (como Antigravity) mantenga el repositorio profesional:

### A. Prompt de Inicio de Tarea (Contextualización)
> "Actúa como un Senior DevOps. Analiza el estado actual del repositorio. Crea un Issue en GitHub para la tarea [NOMBRE_TAREA] con el label [RA_CORRESPONDIENTE] y luego abre una rama de feature usando los scripts de /WorkFlow. Asegúrate de que el código use inglés técnico."

### B. Prompt de Sincronización y Commits (Atomic Updates)
> "Revisa mis cambios locales. Realiza commits atómicos siguiendo el estándar de la rúbrica (en inglés técnico). Si hay nuevos Issues, usa el script `02_Sincronizar_Todo.ps1`. Al finalizar, haz push a la rama de feature y actualiza el estado en el tablero del proyecto."

### C. Prompt de Finalización y Pull Request (RA4.i)
> "He terminado la feature. Ejecuta los tests locales con `Run_Tests.ps1`. Si todo está en verde, usa `05_Crear_PullRequest.ps1` para abrir una PR hacia `develop`. Asegúrate de mencionar los Issues que cierra esta PR."

---

## 📊 Estrategia para el "10" (Resumen Rápido)

| Objetivo | Acción |
| :--- | :--- |
| **No romper main** | Usa `Set_Main_Protection.ps1` al inicio del proyecto. |
| **Javadoc Vivo** | No generes Javadoc manual. Deja que la GitHub Action lo haga en cada push a `main`. |
| **UML Profesional** | Usa `UML_Template.md` y edita los diagramas Mermaid directamente en VS Code. |
| **Inglés Técnico** | Si dudas de un nombre de variable, pide a la IA: *"Refactoriza este método para usar nomenclatura profesional en inglés técnico"*. |

---
**Nota**: Este flujo de trabajo está diseñado para maximizar tu nota minimizando el error manual. ¡Usa los scripts!
