# 00_Core_Loader.ps1
# Engine de carga de configuración con validación de integridad (v3.1)

$ConfigPath = Join-Path $PSScriptRoot "workflow_config.json"

function Write-WFLog {
    param([string]$Message, [string]$Color = "Cyan")
    Write-Host "[WorkFlow] $Message" -ForegroundColor $Color
}

if (!(Test-Path $ConfigPath)) {
    Write-WFLog "ERROR: No se encuentra workflow_config.json en $PSScriptRoot" "Red"
    return
}

try {
    $rawJson = Get-Content $ConfigPath -Raw
    $Global:WFConfig = $rawJson | ConvertFrom-Json -ErrorAction Stop
    
    # Validación de integridad de campos críticos
    if (-not $WFConfig.project.owner -or -not $WFConfig.project.repo_name) {
        throw "Campos de proyecto incompletos en el JSON."
    }

    # Exportar variables globales
    $Global:REPO_OWNER = $WFConfig.project.owner
    $Global:REPO_NAME  = $WFConfig.project.repo_name
    $Global:BRANCH_MAIN = $WFConfig.git_flow.main_branch
    $Global:BRANCH_DEV  = $WFConfig.git_flow.develop_branch
    $Global:PROJECT_NUM = $WFConfig.github_settings.project_number

    Write-WFLog "Entorno cargado: $($WFConfig.project.name)" "Green"
} catch {
    Write-WFLog "ERROR FATAL al cargar configuración: $($_.Exception.Message)" "Red"
    exit
}
