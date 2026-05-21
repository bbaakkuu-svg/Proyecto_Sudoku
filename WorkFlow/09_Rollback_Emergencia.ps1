# 09_Rollback_Emergencia.ps1
# Revierte la rama main al penultimo Tag y destruye el tag actual en caso de fallo critico
. "$PSScriptRoot\00_Core_Loader.ps1"

Write-WFLog "--- 🚨 INICIANDO ROLLBACK DE EMERGENCIA (DISASTER RECOVERY) ---" "Red"

$tags = git tag --sort=-creatordate
if ($tags.Count -lt 2) {
    Write-WFLog "Error: No hay suficientes tags (mínimo 2) para hacer un rollback automático." "Red"
    exit 1
}

$currentTag = $tags[0]
$previousTag = $tags[1]

Write-Host "Tag Critico Actual (Roto): $currentTag" -ForegroundColor Red
Write-Host "Tag Estable Anterior (Seguro): $previousTag" -ForegroundColor Green

$confirm = Read-Host "⚠️ ¿Estas absolutamente seguro de revertir MAIN a $previousTag? (s/n)"
if ($confirm -ne 's') { Write-WFLog "Rollback cancelado." "Yellow"; exit }

Write-WFLog "Paso 1: Eliminando Tag roto remoto y local..." "Yellow"
git tag -d $currentTag
git push origin :refs/tags/$currentTag

Write-WFLog "Paso 2: Revertiendo la rama MAIN a un estado seguro..." "Yellow"
git checkout $Global:BRANCH_MAIN
git reset --hard $previousTag
git push origin $Global:BRANCH_MAIN --force

Write-WFLog "Paso 3: Sincronizando DEVELOP para reflejar el rollback..." "Yellow"
git checkout $Global:BRANCH_DEV
# Fusionar ignorando cambios recientes que rompian main
git merge $Global:BRANCH_MAIN -m "chore(rollback): sync dev with main after emergency rollback to $previousTag"
git push origin $Global:BRANCH_DEV

Write-WFLog "✅ EMERGENCIA CONTENIDA. Produccion ha vuelto a la version $previousTag de forma segura." "Green"
