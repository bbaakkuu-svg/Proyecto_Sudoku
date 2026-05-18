# 08_Publicar_Release.ps1
# Automatiza el versionado, Changelog y lanzamiento de Release (v3.1)
. "$PSScriptRoot\00_Core_Loader.ps1"

Write-WFLog "--- GESTOR DE LANZAMIENTOS (RELEASE) ---" "Cyan"

$version = Read-Host "Número de la nueva versión (ej: 1.1.0)"
if (!$version) { Write-WFLog "Operación cancelada." "Red"; exit }

$tagName = "v$version"
$changelogFile = "CHANGELOG.md"
$fullRepo = "$($WFConfig.project.owner)/$($WFConfig.project.repo_name)"

# 1. Generar Notas de Lanzamiento desde PRs merged
Write-WFLog "Obteniendo Pull Requests terminados desde la última versión..." "Yellow"
$mergedPRs = gh pr list --state merged --limit 20 --json title,number,author --jq '.[] | "- " + .title + " (##" + (.number|tostring) + ") by @" + .author.login'

if (-not $mergedPRs) {
    $releaseNotes = "- Maintenance update and stability improvements."
} else {
    $releaseNotes = $mergedPRs -join "`n"
}

# 2. Actualizar CHANGELOG.md
$date = Get-Date -Format "yyyy-MM-dd"
$newEntry = @"
## [$version] - $date
### Changed & Fixed
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

# 3. Actualizar pom.xml si existe
if (Test-Path "pom.xml") {
    Write-WFLog "Sincronizando versión en pom.xml..." "Gray"
    (Get-Content "pom.xml") -replace '<version>.*</version>', "<version>$version</version>" | Set-Content "pom.xml"
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

Write-WFLog "¡Lanzamiento $tagName completado con éxito!" "Green"
git checkout $Global:BRANCH_DEV
