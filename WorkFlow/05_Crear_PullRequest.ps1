# 05_Crear_PullRequest.ps1 
# Script para crear una solicitud de extraccion (Refactorizado)
. "$PSScriptRoot\00_Core_Loader.ps1"

Write-WFLog "--- CREANDO SOLICITUD DE EXTRACCION (PR) ---"

$ramaActual = git branch --show-current

$issueNum = Read-Host "¿Cierra algun Issue? (Opcional, Nro:)"
$prBody = "Nueva funcionalidad completada siguiendo el Framework WorkFlow."
if (-not [string]::IsNullOrWhiteSpace($issueNum)) {
    $issueNum = $issueNum -replace '#', ''
    $prBody += "`n`nCloses #$issueNum"
}

if ($ramaActual -match "^hotfix/") {
    Write-WFLog "Detectada rama hotfix. Creando PR hacia main y develop automaticamente..." "Cyan"
    
    # PR a main
    Write-WFLog "-> PR a $Global:BRANCH_MAIN" "Yellow"
    gh pr create --base $Global:BRANCH_MAIN --head $ramaActual --title "HOTFIX: Fusionar $ramaActual en $Global:BRANCH_MAIN" --body $prBody
    
    # PR a develop
    Write-WFLog "-> PR a $Global:BRANCH_DEV" "Yellow"
    gh pr create --base $Global:BRANCH_DEV --head $ramaActual --title "HOTFIX: Fusionar $ramaActual en $Global:BRANCH_DEV" --body $prBody
    
} elseif ($ramaActual -match "^feature/") {
    Write-WFLog "Detectada rama feature. Creando PR hacia $Global:BRANCH_DEV automaticamente..." "Cyan"
    $target = $Global:BRANCH_DEV
    gh pr create --base $target --head $ramaActual --title "FEATURE: Fusionar $ramaActual en $target" --body $prBody
    
} else {
    $base = Read-Host "Rama destino? (1: $Global:BRANCH_DEV, 2: $Global:BRANCH_MAIN)"
    $target = if ($base -eq "2") { $Global:BRANCH_MAIN } else { $Global:BRANCH_DEV }
    Write-WFLog "Origen: $ramaActual | Destino: $target"
    gh pr create --base $target --head $ramaActual --title "Fusionar $ramaActual en $target" --body $prBody
}

Write-WFLog "Pull Request creado. Revisa tu panel en GitHub." "Green"
