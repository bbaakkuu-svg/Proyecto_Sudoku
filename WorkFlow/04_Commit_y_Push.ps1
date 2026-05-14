# 04_Commit_y_Push.ps1 
# Script de Guardado con Auto-Linter de Commits Atómicos (RA1.e)
. "$PSScriptRoot\00_Core_Loader.ps1"

Write-WFLog "--- COMMIT LINTER & PUSH (v3.1) ---" "Cyan"

$mensaje = Read-Host "Commit Message (Technical English Required)"

# 1. Validar Longitud Mínima
if ($mensaje.Length -lt $WFConfig.commit_lint.min_length) {
    Write-WFLog "ERROR: El mensaje es demasiado corto (mínimo $($WFConfig.commit_lint.min_length) caracteres)." "Red"
    exit 1
}

# 2. Validar Palabras Prohibidas (Español)
foreach ($word in $WFConfig.commit_lint.forbidden_words) {
    if ($mensaje -match $word) {
        Write-WFLog "ERROR: Has usado una palabra prohibida o no técnica en español: '$word'." "Red"
        Write-WFLog "Acción: Usa inglés técnico profesional (ej: 'fix' en lugar de 'arreglo')." "Yellow"
        exit 1
    }
}

# 3. Validar Patrón de Commits Atómicos (Conventional Commits)
$patternFound = $false
foreach ($pattern in $WFConfig.commit_lint.patterns) {
    if ($mensaje.StartsWith($pattern)) {
        $patternFound = $true
        break
    }
}

if (-not $patternFound) {
    Write-WFLog "ERROR: El mensaje debe empezar con un patrón válido: $($WFConfig.commit_lint.patterns -join ', ')." "Red"
    Write-WFLog "Ejemplo: 'feat: implement row validation logic'" "Yellow"
    exit 1
}

# 4. Proceso de Git
Write-WFLog "--- LINT PASADO CON ÉXITO ---" "Green"
git add .
git commit -m $mensaje

$branch = git branch --show-current
Write-WFLog "Sincronizando con nube (push)..." "Yellow"
git push origin $branch

if ($LASTEXITCODE -eq 0) {
    Write-WFLog "Proyecto actualizado en GitHub (Rama: $branch)." "Green"
} else {
    Write-WFLog "Error al subir a GitHub. Verifica tu conexión o conflictos." "Red"
}
