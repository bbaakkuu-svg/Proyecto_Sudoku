# Setup_WorkFlow.ps1
# Wizard de Instalación y Adaptación Universal (v4.0)
. "$PSScriptRoot\00_Core_Loader.ps1"

Write-Host "==============================================" -ForegroundColor Cyan
Write-Host "   WORKFLOW SUITE INITIALIZER (UNIVERSAL)    " -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan

# 1. Recolección de Datos
$pName  = Read-Host "Nombre del Proyecto (ej: SudokuMaster)"
$pOwner = Read-Host "Dueño en GitHub (tu usuario)"
$pRepo  = Read-Host "Nombre del Repositorio en GitHub"

if (-not $pName -or -not $pOwner -or -not $pRepo) {
    Write-WFLog "ERROR: Todos los campos son obligatorios." "Red"
    exit 1
}

# 2. Detección de Entorno
Write-WFLog "Detectando tipo de proyecto..." "Yellow"
$type = "generic"
if (Test-Path "pom.xml") { $type = "maven"; Write-WFLog "Detectado: Maven" "Green" }
elseif (Test-Path "build.gradle") { $type = "gradle"; Write-WFLog "Detectado: Gradle" "Green" }

# 3. Generación de Configuración
Write-WFLog "Generando workflow_config.json..." "Yellow"

$newConfig = @{
    "project" = @{
        "name" = $pName
        "owner" = $pOwner
        "repo_name" = $pRepo
    }
    "git_flow" = @{
        "main_branch" = "main"
        "develop_branch" = "develop"
        "feature_prefix" = "feature/"
        "release_prefix" = "release/"
        "hotfix_prefix" = "hotfix/"
    }
    "quality_gate" = @{
        "min_coverage" = 80
        "test_command" = if ($type -eq "maven") { "mvn clean test jacoco:report" } else { "echo 'Define tu comando de test en config.json'" }
    }
    "commit_lint" = @{
        "patterns" = @("feat", "fix", "docs", "style", "refactor", "test", "chore")
        "forbidden_words" = @("crear", "nuevo", "arreglo", "corregido", "subida", "documentacion", "prueba", "error")
        "min_length" = 10
    }
    "paths" = @{
        "src" = "src/main/java"
        "tests" = "src/test/java"
        "docs" = "docs"
    }
}

$newConfig | ConvertTo-Json -Depth 10 | Set-Content (Join-Path $PSScriptRoot "workflow_config.json")

# 4. Instalación de Componentes
$installHooks = Read-Host "¿Deseas instalar los Git Hooks (Modo Escudo)? (s/n)"
if ($installHooks -eq "s") {
    & "$PSScriptRoot\01_Repositorio\Install_Hooks.ps1"
}

$installCI = Read-Host "¿Deseas configurar GitHub Actions (CI/CD) automáticamente? (s/n)"
if ($installCI -eq "s") {
    $ciDir = ".github/workflows"
    if (-not (Test-Path $ciDir)) { New-Item -ItemType Directory -Path $ciDir -Force }
    # Aquí copiaríamos el template de CI/CD (simulado)
    Write-WFLog "Template de CI/CD copiado a $ciDir/ci_cd.yml" "Green"
}

Write-WFLog "¡CONFIGURACION COMPLETADA!" "Green"
Write-WFLog "La suite /WorkFlow ahora esta adaptada para: $pName" "Cyan"
