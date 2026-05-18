# Script para crear las nuevas Issues de NutriPlan Pro (Fase Avanzada)

$repo = "bbaakkuu-svg/Mi_app_Java-Swing"

$issues = @(
    @{title="Tarea 6.1: Integraciion con SQLite"; body="Migrar la persistencia de JSON a una base de datos SQLite para soportar historiales y mltiples perfiles."},
    @{title="Tarea 6.2: Exportacin de Lista de Compra"; body="Implementar funcionalidad para exportar la lista de compra consolidada a un archivo .txt o .pdf compartido."},
    @{title="Tarea 6.3: Editor de Dieta Integrado"; body="Crear un formulario en el Dashboard que permita modificar platos e ingredientes directamente desde la aplicacin."},
    @{title="Tarea 6.4: Estadsticas Nutricionales"; body="Aadir un panel con JFreeChart para visualizar el balance de caloras o tipos de alimentos de la semana."},
    @{title="Tarea 6.5: Estructura Multiplataforma (Android Ready)"; body="Refactorizar la lgica de negocio a un mdulo 'core' independiente para facilitar la futura portabilidad a Android."},
    @{title="Tarea 6.6: Sistema de Notificaciones"; body="Implementar recordatorios en Swing (SystemTray) para las tomas de media maana y merienda."},
    @{title="Tarea 6.7: Diseo Premium (Glassmorphism)"; body="Aplicar efectos de transparencia y bordes redondeados avanzados para elevar la esttica del Dashboard."}
)

foreach ($issue in $issues) {
    Write-Host "Creando: $($issue.title)..."
    gh issue create --repo $repo --title $issue.title --body $issue.body
}

Write-Host "¡Nuevas tareas añadidas a GitHub!"
