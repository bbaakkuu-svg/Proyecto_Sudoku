# 02_Sincronizar_Todo.ps1 
# Sincroniza Issues con el Tablero del Proyecto V2 (RA4.f, h)
. "$PSScriptRoot\00_Core_Loader.ps1"

$fullRepo = "$($WFConfig.project.owner)/$($WFConfig.project.repo_name)"
$projNum = $WFConfig.github_settings.project_number
$owner = $WFConfig.project.owner

Write-WFLog "--- SINCRONIZACIÓN MAESTRA DE PROYECTO ---" "Cyan"
Write-WFLog "Proyecto: #$projNum | Repo: $fullRepo" "Gray"

# Obtener tareas abiertas
$urls = gh issue list --repo $fullRepo --limit 50 --json url --jq '.[].url'

if (-not $urls) {
    Write-WFLog "No se encontraron tareas abiertas para sincronizar." "Yellow"
} else {
    foreach ($url in $urls) {
        Write-WFLog "Vinculando Issue: $url ..." "Gray"
        # gh project item-add requiere el número de proyecto y el owner
        gh project item-add $projNum --owner $owner --url $url | Out-Null
    }
    Write-WFLog "¡Sincronización completada! Tablero actualizado." "Green"
}
