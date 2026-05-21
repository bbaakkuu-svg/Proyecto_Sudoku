# devops.ps1
# Orquestador Central de Antigravity DevOps Framework (Agnostic CLI)
# Este script provee una interfaz unificada para todas las herramientas del ciclo CI/CD.

$scriptPath = $PSScriptRoot
$workflowDir = "$scriptPath\WorkFlow"

function Show-Menu {
    Clear-Host
    Write-Host "==========================================================" -ForegroundColor Cyan
    Write-Host " 🚀 ANTIGRAVITY DEVOPS FRAMEWORK (Agnostic CI/CD Tooling) " -ForegroundColor Cyan
    Write-Host "==========================================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host " [1] 🌿 Iniciar Tarea      (Generador GitFlow)"
    Write-Host " [2] 💾 Guardar Trabajo    (Commit Linter, SAST & Tests)"
    Write-Host " [3] 📤 Enviar a Revisión  (Smart Pull Request)"
    Write-Host " [4] 🧹 Limpiar Entorno    (Marcar Done y Podar Ramas)"
    Write-Host " [5] 📦 Lanzar Producción  (SemVer Release & Changelog)"
    Write-Host " [6] 🛡️ Sincronizar Tablero(Update Issues en GitHub)"
    Write-Host " [7] 🚨 Rollback Emergencia(Disaster Recovery)"
    Write-Host " [0] ❌ Salir"
    Write-Host ""
    Write-Host "==========================================================" -ForegroundColor Cyan
}

while ($true) {
    Show-Menu
    $choice = Read-Host "👉 Selecciona una opcion [0-6]"

    switch ($choice) {
        "1" { 
            powershell -NoProfile -ExecutionPolicy Bypass -File "$workflowDir\03_Nueva_Rama.ps1"
            Pause 
        }
        "2" { 
            powershell -NoProfile -ExecutionPolicy Bypass -File "$workflowDir\04_Commit_y_Push.ps1"
            Pause 
        }
        "3" { 
            powershell -NoProfile -ExecutionPolicy Bypass -File "$workflowDir\05_Crear_PullRequest.ps1"
            Pause 
        }
        "4" { 
            powershell -NoProfile -ExecutionPolicy Bypass -File "$workflowDir\07_Marcar_Done.ps1"
            Pause 
        }
        "5" { 
            powershell -NoProfile -ExecutionPolicy Bypass -File "$workflowDir\08_Publicar_Release.ps1"
            Pause 
        }
        "6" { 
            powershell -NoProfile -ExecutionPolicy Bypass -File "$workflowDir\02_Sincronizar_Todo.ps1"
            Pause 
        }
        "7" { 
            powershell -NoProfile -ExecutionPolicy Bypass -File "$workflowDir\09_Rollback_Emergencia.ps1"
            Pause 
        }
        "0" { Write-Host "Saliendo del Framework DevOps..."; exit }
        default { Write-Host "❌ Opcion invalida. Intenta de nuevo." -ForegroundColor Red; Start-Sleep -Seconds 2 }
    }
}
