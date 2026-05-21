# 08_Publicar_Release.ps1
# Automatiza el versionado, Changelog y lanzamiento de Release (v3.1)
. "$PSScriptRoot\00_Core_Loader.ps1"

Write-WFLog "--- GESTOR DE LANZAMIENTOS (RELEASE) ---" "Cyan"

$versionFile = $WFConfig.tech_stack.version_file
if (Test-Path $versionFile) {
    if ($versionFile -match "\.xml$") {
        [xml]$fileContent = Get-Content $versionFile
        $currentVersionStr = $fileContent.project.version -replace '-SNAPSHOT', ''
    } elseif ($versionFile -match "\.json$") {
        $fileContent = Get-Content $versionFile | ConvertFrom-Json
        $currentVersionStr = $fileContent.version
    } else {
        $currentVersionStr = "1.0.0"
    }
} else {
    $currentVersionStr = "1.0.0"
}
$parts = $currentVersionStr.Split('.')
if ($parts.Length -lt 3) {
    if ($parts.Length -eq 2) { $parts += '0' }
    elseif ($parts.Length -eq 1) { $parts += '0'; $parts += '0' }
    else { $parts = @('1', '0', '0') }
}
$major = [int]$parts[0]
$minor = [int]$parts[1]
$patch = [int]$parts[2]

Write-Host "Versión actual: $currentVersionStr" -ForegroundColor Cyan
Write-Host "Selecciona el nivel de impacto del Release (SemVer):" -ForegroundColor Yellow
Write-Host "[1] Mayor (MAJOR) -> $(($major + 1)).0.0 (Cambios arquitectonicos/disruptivos)"
Write-Host "[2] Menor (MINOR) -> $major.$(($minor + 1)).0 (Nuevas funcionalidades retrocompatibles)"
Write-Host "[3] Parche (PATCH) -> $major.$minor.$(($patch + 1)) (Correccion de bugs)"
Write-Host "[4] Manual -> Introducir version personalizada"

$choice = Read-Host "Elige una opcion (1-4)"
switch ($choice) {
    "1" { $newVersion = "$(($major + 1)).0.0" }
    "2" { $newVersion = "$major.$(($minor + 1)).0" }
    "3" { $newVersion = "$major.$minor.$(($patch + 1))" }
    "4" { $newVersion = Read-Host "Introduce la version final (ej: 2.1.0)" }
    default { Write-WFLog "Seleccion invalida. Cancelando." "Red"; exit }
}

if ([string]::IsNullOrWhiteSpace($newVersion)) { Write-WFLog "Operacion cancelada." "Red"; exit }
$version = $newVersion
$tagName = "v$version"
$changelogFile = "CHANGELOG.md"
$fullRepo = "$($WFConfig.project.owner)/$($WFConfig.project.repo_name)"

# 1. Generar Notas de Lanzamiento desde Git Log
Write-WFLog "Generando Changelog desde el historial de commits atomicos..." "Yellow"
$lastTag = git describe --tags --abbrev=0 2>$null
$logRange = if ($lastTag) { "$lastTag..HEAD" } else { "HEAD" }

$commits = git log $logRange --no-merges --pretty=format:"%s"

$feats = @()
$fixes = @()
$others = @()

if ($commits) {
    foreach ($msg in $commits) {
        if ($msg -match "^feat(\(.*\))?:") { $feats += "- $($msg)" }
        elseif ($msg -match "^fix(\(.*\))?:") { $fixes += "- $($msg)" }
        elseif ($msg -match "^(docs|style|refactor|test|chore)(\(.*\))?:") { $others += "- $($msg)" }
    }
}

$releaseNotes = ""
if ($feats.Count -gt 0) { $releaseNotes += "### ✨ Nuevas Caracteristicas`n$($feats -join "`n")`n`n" }
if ($fixes.Count -gt 0) { $releaseNotes += "### 🐛 Correcciones de Errores`n$($fixes -join "`n")`n`n" }
if ($others.Count -gt 0) { $releaseNotes += "### 🛠️ Mantenimiento y Otros`n$($others -join "`n")`n`n" }

if ([string]::IsNullOrWhiteSpace($releaseNotes)) {
    $releaseNotes = "### 🛠️ Mantenimiento`n- Actualizacion general y mejoras de estabilidad.`n`n"
}

# 2. Actualizar CHANGELOG.md
$date = Get-Date -Format "yyyy-MM-dd"
$newEntry = @"
## [$version] - $date
$releaseNotes
"@

if (Test-Path $changelogFile) {
    Write-WFLog "Actualizando $changelogFile..." "Gray"
    $currentContent = Get-Content $changelogFile -Raw
    $newContent = "# Changelog`n`n$newEntry$($currentContent -replace '^# Changelog\s*', '')"
    $newContent | Set-Content $changelogFile
} else {
    Write-WFLog "Creando nuevo $changelogFile..." "Gray"
    "# Changelog`n`n$newEntry" | Set-Content $changelogFile
}

# 3. Actualizar archivo de version del stack
if (Test-Path $WFConfig.tech_stack.version_file) {
    Write-WFLog "Sincronizando version en $($WFConfig.tech_stack.version_file) ($($WFConfig.tech_stack.language.ToUpper()))..." "Gray"
    $versionCmd = $WFConfig.tech_stack.version_set_command -replace '\{VERSION\}', $version
    Invoke-Expression $versionCmd
}

# 4. GitFlow: Merge a Main y Tagging
Write-WFLog "Consolidando cambios en Git..." "Yellow"
git add .
git commit -m "chore(release): prepare release $tagName [skip ci]"
git tag -a $tagName -m "Release $tagName"

Write-WFLog "Fusionando con rama MAIN..." "Gray"
git checkout $Global:BRANCH_MAIN
git merge $Global:BRANCH_DEV
git push origin $Global:BRANCH_MAIN $Global:BRANCH_DEV --tags

# 5. Crear Release en GitHub
Write-WFLog "Publicando Release en GitHub..." "Green"
gh release create $tagName --title "$($WFConfig.project.name) $tagName" --notes "## What's Changed`n$releaseNotes"

# 6. ChatOps: Notificacion Webhook
$discordUrl = $WFConfig.webhooks.discord_release_url
if (-not [string]::IsNullOrWhiteSpace($discordUrl)) {
    Write-WFLog "Enviando notificacion a Discord..." "Gray"
    $payload = @{
        embeds = @(
            @{
                title = "🚀 Nueva Version Lanzada: $($WFConfig.project.name) $tagName"
                color = 3066993
                description = "El codigo ha sido desplegado a produccion exitosamente.`n`n**Notas de la Version:**`n$releaseNotes"
            }
        )
    } | ConvertTo-Json -Depth 5
    Invoke-RestMethod -Uri $discordUrl -Method Post -Body $payload -ContentType 'application/json' | Out-Null
}

Write-WFLog "¡Lanzamiento $tagName completado con éxito!" "Green"
git checkout $Global:BRANCH_DEV
