# 🚀 Guía de Portabilidad: WorkFlow Suite (v4.0)

Esta suite ha sido diseñada como un módulo "Plug & Play". Puedes llevarte la carpeta `/WorkFlow` a cualquier repositorio de Java y tener un entorno DevOps profesional en segundos.

## 📦 Cómo exportar la suite

1.  **Copia la carpeta**: Simplemente copia la carpeta `/WorkFlow` íntegra a la raíz de tu nuevo repositorio.
2.  **Limpia la configuración previa**: Borra el archivo `workflow_config.json` si ya existe (el wizard lo creará de nuevo).
3.  **Asegúrate de tener Git**: El repositorio destino debe estar inicializado con `git init`.

## 🛠️ Instalación en el nuevo proyecto

Desde una terminal de PowerShell en la raíz del nuevo proyecto, ejecuta:

```powershell
.\WorkFlow\Setup_WorkFlow.ps1
```

### ¿Qué hará el Wizard?
1.  **Contextualización**: Te pedirá el nombre del proyecto y los datos de GitHub para configurar la integración con el tablero de proyectos y las releases.
2.  **Auto-detección**: Sabrá si usas **Maven** o **Gradle** y ajustará las rutas de los reportes de cobertura automáticamente.
3.  **Instalación de Hooks**: Configurará el **Modo Escudo** (pre-commit y commit-msg) para que el nuevo repo esté protegido desde el primer commit.
4.  **CI/CD Ready**: Preparará los archivos necesarios para que GitHub Actions empiece a trabajar de inmediato.

## ⚙️ Adaptación de Reglas Especiales

Si el nuevo proyecto requiere un estándar de commits diferente o un umbral de cobertura distinto (ej: 90%), solo tienes que editar el `workflow_config.json` generado. No necesitas tocar ni una sola línea de código de los scripts (`.ps1` o `.sh`).

---
**WorkFlow Suite** - Profesionalizando el desarrollo, un repositorio a la vez.
