# 04_Commit_y_Push.ps1 
# Script de Guardado con Auto-Linter de Commits Atómicos (RA1.e)
. "$PSScriptRoot\00_Core_Loader.ps1"

Write-WFLog "--- COMMIT LINTER & PUSH (v3.1) ---" "Cyan"

Write-Host "Selecciona el tipo de cambio:" -ForegroundColor Yellow
$patterns = $WFConfig.commit_lint.patterns
for ($i = 0; $i -lt $patterns.Count; $i++) {
    Write-Host "[$($i + 1)] $($patterns[$i])"
}
$typeIndex = Read-Host "Elige un numero (1-$($patterns.Count))"
$typeIndexInt = 0
if ([int]::TryParse($typeIndex, [ref]$typeIndexInt) -and $typeIndexInt -ge 1 -and $typeIndexInt -le $patterns.Count) {
    $selectedType = $patterns[$typeIndexInt - 1]
} else {
    Write-WFLog "ERROR: Seleccion invalida. Se usara 'chore' por defecto." "Red"
    $selectedType = "chore"
}

$scope = Read-Host "Alcance/Scope opcional (ej: ui, db, auth) [Enter para omitir]"
if ([string]::IsNullOrWhiteSpace($scope)) {
    $commitPrefix = "$($selectedType):"
} else {
    $commitPrefix = "$($selectedType)($scope):"
}

$description = Read-Host "Descripcion del cambio en Ingles Tecnico"

$issueNum = Read-Host "¿Cierra algun Issue? (Opcional, Nro:)"
if (-not [string]::IsNullOrWhiteSpace($issueNum)) {
    $issueNum = $issueNum -replace '#', ''
    $description = "$description (Closes #$issueNum)"
}

$mensaje = "$commitPrefix $description"
Write-WFLog "Commit ensamblado: '$mensaje'" "Cyan"

# 1. Validar Longitud Minima
if ($description.Length -lt $WFConfig.commit_lint.min_length) {
    Write-WFLog "ERROR: La descripcion es demasiado corta (minimo $($WFConfig.commit_lint.min_length) caracteres)." "Red"
    exit 1
}

# 2. Validar Palabras Prohibidas (Espanol)
foreach ($word in $WFConfig.commit_lint.forbidden_words) {
    if ($mensaje -match $word) {
        Write-WFLog "ERROR: Has usado una palabra prohibida o no tecnica en espanol: '$word'." "Red"
        Write-WFLog "Accion: Usa ingles tecnico profesional (ej: 'fix' en lugar de 'arreglo')." "Yellow"
        exit 1
    }
}

# 3. Escaneo de Secretos (Security Gate)
Write-WFLog "--- ESCANEANDO SECRETOS LOCALES ---" "Yellow"
$modifiedFiles = git diff --name-only
$untrackedFiles = git ls-files --others --exclude-standard
$allFilesToScan = $modifiedFiles + $untrackedFiles | Sort-Object -Unique

foreach ($file in $allFilesToScan) {
    if (Test-Path $file) {
        $content = Get-Content $file -Raw
        foreach ($pattern in $WFConfig.security_scanner.blocked_patterns) {
            if ($content -match $pattern) {
                Write-WFLog "ERROR DE SEGURIDAD CRITICO: Posible secreto detectado." "Red"
                Write-WFLog "Archivo: $file" "Red"
                Write-WFLog "Patron bloqueado: $pattern" "Yellow"
                Write-WFLog "Accion: El commit ha sido cancelado para proteger tus credenciales." "Red"
                exit 1
            }
        }
    }
}
Write-WFLog "Ningun secreto detectado. Codigo limpio." "Green"

# 4. Auto-Formateo de Codigo
Write-WFLog "--- APLICANDO FORMATADOR ($($WFConfig.tech_stack.language.ToUpper())) ---" "Cyan"
Invoke-Expression $WFConfig.tech_stack.format_command
Write-WFLog "Codigo estandarizado y limpio." "Green"

# 5. Validar Tests Locales (Pre-Push Gate)
Write-WFLog "--- EJECUTANDO TESTS LOCALES (Fail-Fast) ---" "Yellow"
Invoke-Expression $WFConfig.tech_stack.test_command
if ($LASTEXITCODE -ne 0) {
    Write-WFLog "ERROR: Los tests unitarios han fallado." "Red"
    Write-WFLog "Accion: El commit ha sido cancelado. Corrige el codigo localmente y vuelve a intentar para proteger la rama remota." "Yellow"
    exit 1
}
Write-WFLog "Tests locales superados exitosamente." "Green"

# 6. Proceso de Git
Write-WFLog "--- LINT Y TESTS PASADOS CON EXITO ---" "Green"
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
