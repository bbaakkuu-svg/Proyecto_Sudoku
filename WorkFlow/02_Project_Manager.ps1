# 02_Project_Manager.ps1
# Gestor Avanzado de GitHub Projects V2 (v5.0)
. "$PSScriptRoot\00_Core_Loader.ps1"

function Show-Help {
    Write-WFLog "Uso: .\02_Project_Manager.ps1 [init | sync | meta | bulk-edit]" "Cyan"
    Write-WFLog "  init      : Crea un nuevo proyecto organizacional o de usuario." "Gray"
    Write-WFLog "  sync      : Busca y añade masivamente Issues/PRs al proyecto." "Gray"
    Write-WFLog "  meta      : Actualiza descripción y README del proyecto." "Gray"
    Write-WFLog "  bulk-edit : Edita campos de forma masiva (ej: Status)." "Gray"
}

$action = $args[0]
if (-not $action) { Show-Help; exit }

$owner = $WFConfig.project.owner
$projNum = $WFConfig.github_settings.project_number

# --- ACCIONES ---

if ($action -eq "init") {
    Write-WFLog "Creando nuevo GitHub Project V2..." "Yellow"
    $title = Read-Host "Título del Proyecto"
    $isOrg = Read-Host "¿Es una Organización? (s/n)"
    
    $cmd = "gh project create --owner $owner --title `"$title`""
    if ($isOrg -eq "s") { $cmd += " --org $owner" }
    
    $result = Invoke-Expression $cmd | ConvertFrom-Json
    Write-WFLog "¡Proyecto creado! Número: $($result.number)" "Green"
    Write-WFLog "Actualiza tu workflow_config.json con el número $($result.number)" "Yellow"
}

elseif ($action -eq "sync") {
    Write-WFLog "Sincronización Masiva: Buscando Issues y PRs..." "Yellow"
    $repo = "$($WFConfig.project.owner)/$($WFConfig.project.repo_name)"
    
    # Obtener todas las URLs de Issues y PRs abiertos
    $items = gh api -X GET search/issues -f q="repo:$repo is:open" --jq '.items[].html_url'
    
    foreach ($url in $items) {
        Write-WFLog "Agregando: $url" "Gray"
        gh project item-add $projNum --owner $owner --url $url | Out-Null
    }
    Write-WFLog "Sincronización masiva completada." "Green"
}

elseif ($action -eq "meta") {
    Write-WFLog "Actualizando metadatos del proyecto..." "Yellow"
    $desc = Read-Host "Nueva Descripción"
    # README no es soportado directamente por 'gh project edit' pero podemos usar la API si fuera necesario.
    # Por ahora, editamos la descripción básica.
    gh project edit $projNum --owner $owner --description "$desc"
    Write-WFLog "Descripción actualizada." "Green"
}

elseif ($action -eq "bulk-edit") {
    Write-WFLog "--- EDICIÓN MASIVA DE CAMPOS ---" "Yellow"
    $field = Read-Host "Nombre del campo (ej: Status)"
    $value = Read-Host "Nuevo valor (ej: Todo / In Progress / Done)"
    
    # 1. Obtener items del proyecto
    $items = gh project item-list $projNum --owner $owner --format json | ConvertFrom-Json
    
    foreach ($item in $items) {
        Write-WFLog "Actualizando item $($item.id)..." "Gray"
        gh project item-edit --id $($item.id) --project-number $projNum --owner $owner --field "$field" --single-select-option "$value" | Out-Null
    }
    Write-WFLog "Edición masiva finalizada." "Green"
}

else {
    Show-Help
}
