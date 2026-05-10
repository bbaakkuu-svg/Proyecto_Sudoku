# 🛠️ WorkFlow Sudoku Elite: DevOps para Entornos de Desarrollo
**Proyecto:** Sudoku Java (Individual Improvement Test)  
**Versión:** 3.0 (Rubric-Aligned Edition)

Este directorio es un ecosistema de automatización diseñado para cumplir con los Resultados de Aprendizaje (RA) del módulo de Entornos de Desarrollo. Centraliza la lógica de GitFlow, QA, CI/CD y Documentación Técnica Viva.

---

## 📂 Inventario de Herramientas (Rubric Match)

| Script / Herramienta | Propósito | RA Asociado |
| :--- | :--- | :--- |
| `00_Core_Loader.ps1` | Motor de carga de configuración y entorno. | RA4 |
| `01_Repositorio/Set_Main_Protection.ps1` | Configura reglas de protección en `main`. | RA4.f, h, i |
| `02_Quality_QA/Run_Tests.ps1` | Ejecuta JUnit + JaCoCo localmente. | RA3.b, f, g |
| `02_Sincronizar_Todo.ps1` | Sincroniza Issues con el GitHub Project V2. | RA4.f, h |
| `03_Nueva_Rama.ps1` | Crea ramas bajo el estándar estricto de GitFlow. | RA4.f, h |
| `04_Commit_y_Push.ps1` | Commits atómicos con mensajes profesionales. | RA1.e |
| `05_Crear_PullRequest.ps1` | Automatiza la creación de PRs hacia `develop`/`main`. | RA4.i |
| `05_Docs/UML_Template.md` | Plantillas Mermaid para UML Vivo. | RA5, RA6 |
| `.github/workflows/ci_cd.yml` | Integración Continua (Build, Test, Javadoc, Pages). | RA4.i, RA2.e |
| `MANUAL_DE_USO.md` | Guía de uso y Prompts Expertos para la IA. | RA6.d |

---

## 🎓 Alineación con la Rúbrica para el "10"

1.  **GitFlow Estricto**: Uso obligatorio de `feature/`, `release/`, etc. Prohibido push directo a `main`.
2.  **UML Vivo**: Diagramas en Mermaid integrados en Markdown dentro de la carpeta `/docs`.
3.  **QA Automatizado**: Cada push a `develop` dispara el flujo de CI que valida pruebas JUnit y cobertura.
4.  **Documentación Técnica**: Javadoc autogenerado y desplegado en GitHub Pages mediante GitHub Actions.
5.  **Inglés Técnico**: Todos los scripts y mensajes de commit deben usar nomenclatura técnica en inglés.

---

## ⚡ Guía de Inicio Rápido
1.  Asegúrate de tener instalada la **GitHub CLI (gh)** y **Maven**.
2.  Personaliza `workflow_config.json` si es necesario.
3.  Ejecuta `.\WorkFlow\00_Core_Loader.ps1` para inicializar el entorno.
4.  **IMPORTANTE**: Consulta el `MANUAL_DE_USO.md` para aprender los comandos y prompts de IA.

---
Generado por Antigravity AI Engine | v3.0 Rubric Edition
