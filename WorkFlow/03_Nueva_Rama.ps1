# 03_Nueva_Rama.ps1 
# Script para crear ramas profesionales (GitFlow Strict v3.1)
. "$PSScriptRoot\00_Core_Loader.ps1"

Write-WFLog "--- GESTOR DE RAMAS GITFLOW ---" "Cyan"

# Comprobar si hay cambios sin commitear
$status = git status --porcelain
if ($status) {
    Write-WFLog "AVISO: Tienes cambios sin commitear. Se recomienda limpiar antes de crear ramas." "Yellow"
}

$tipo = Read-Host "Tipo de rama? (1: feature, 2: release, 3: hotfix)"
$nombreRaw = Read-Host "Nombre descriptivo (técnico, ej: fix-validation)"
# Saneamiento: Solo letras, números y guiones
$nombre = $nombreRaw -replace '[^a-zA-Z0-9-]', ''

if ($nombre -ne $nombreRaw) {
    Write-WFLog "Aviso: Se ha saneado el nombre a '$nombre' (caracteres no permitidos eliminados)." "Yellow"
}

if (!$nombre) { Write-WFLog "Nombre inválido." "Red"; exit 1 }

$prefix = switch($tipo) {
    "1" { 
        git checkout $Global:BRANCH_DEV
        git pull origin $Global:BRANCH_DEV
        $WFConfig.git_flow.feature_prefix 
    }
    "2" { 
        git checkout $Global:BRANCH_DEV
        $WFConfig.git_flow.release_prefix 
    }
    "3" { 
        git checkout $Global:BRANCH_MAIN
        git pull origin $Global:BRANCH_MAIN
        $WFConfig.git_flow.hotfix_prefix 
    }
    Default { 
        Write-WFLog "Tipo no válido." "Red"
        exit 
    }
}

$branchName = "$prefix$nombre"

# Verificar si la rama ya existe
$exists = git branch --list $branchName
if ($exists) {
    Write-WFLog "ERROR: La rama '$branchName' ya existe localmente." "Red"
    exit
}

Write-WFLog "--- CREANDO RAMA: $branchName ---" "Cyan"
git checkout -b $branchName

Write-WFLog "Rama '$branchName' creada correctamente." "Green"
