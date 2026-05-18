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

# 3. Auto-Formateo de Codigo
Write-WFLog "--- APLICANDO FORMATO GOOGLE JAVA ---" "Cyan"
mvn spotless:apply -q
Write-WFLog "Codigo estandarizado y limpio." "Green"

# 4. Validar Tests Locales (Pre-Push Gate)
Write-WFLog "--- EJECUTANDO TESTS LOCALES (Fail-Fast) ---" "Yellow"
mvn test -q
if ($LASTEXITCODE -ne 0) {
    Write-WFLog "ERROR: Los tests unitarios han fallado." "Red"
    Write-WFLog "Accion: El commit ha sido cancelado. Corrige el codigo localmente y vuelve a intentar para proteger la rama remota." "Yellow"
    exit 1
}
Write-WFLog "Tests locales superados exitosamente." "Green"

# 4. Proceso de Git
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
