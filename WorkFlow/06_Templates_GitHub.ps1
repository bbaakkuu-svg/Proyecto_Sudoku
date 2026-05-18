# 06_Templates_GitHub.ps1 
# Script para generar plantillas de GitHub profesionales (Refactorizado)
. "$PSScriptRoot\00_Core_Loader.ps1"

$githubPath = Join-Path (Get-Location) ".github"
$issuePath = Join-Path $githubPath "ISSUE_TEMPLATE"

if (-not (Test-Path $githubPath)) { New-Item -ItemType Directory -Path $githubPath }
if (-not (Test-Path $issuePath)) { New-Item -ItemType Directory -Path $issuePath }

Write-WFLog "--- GENERANDO PLANTILLAS DE REPOSITORIO PARA $($WFConfig.project.name) ---"

$featureTemplate = @"
name: Feature Request
description: Suggest an idea for $($WFConfig.project.name)
title: "[FEATURE] "
labels: ["enhancement"]
body:
  - type: markdown
    attributes:
      value: "Thank you for suggesting an improvement to the project."
"@
$featureTemplate | Out-File (Join-Path $issuePath "feature.yml") -Encoding utf8

$bugTemplate = @"
name: Bug Report
description: Create a report to help us improve $($WFConfig.project.name)
title: "[BUG] "
labels: ["bug"]
body:
  - type: textarea
    id: error
    attributes:
      label: Bug details and reproduction steps
"@
$bugTemplate | Out-File (Join-Path $issuePath "bug.yml") -Encoding utf8

Write-WFLog "Plantillas generadas siguiendo el estandar del proyecto." "Green"
