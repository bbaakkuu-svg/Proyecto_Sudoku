# Run_Tests.ps1
# Ejecuta pruebas JUnit y valida Quality Gate de cobertura (RA3)
. "$PSScriptRoot\..\00_Core_Loader.ps1"

Write-WFLog "--- INICIANDO CONTROL DE CALIDAD (QA) CON QUALITY GATE ---" "Cyan"

# Verificar pom.xml
if (-not (Test-Path "pom.xml")) {
    Write-WFLog "Error: No se encontró pom.xml." "Red"
    exit 1
}

# 1. Ejecutar Maven
Write-WFLog "Ejecutando Maven Test + JaCoCo..." "Yellow"
mvn clean test jacoco:report

if ($LASTEXITCODE -ne 0) {
    Write-WFLog "ERROR: Las pruebas unitarias han fallado. Corrígelas antes de continuar." "Red"
    exit 1
}

# 2. Validar Quality Gate (Cobertura)
$jacocoXml = "target/site/jacoco/jacoco.xml"
if (Test-Path $jacocoXml) {
    [xml]$xml = Get-Content $jacocoXml
    
    # Obtener contador de instrucciones (INSTRUCTION)
    $counter = $xml.report.counter | Where-Object { $_.type -eq "INSTRUCTION" }
    
    $missed = [double]$counter.missed
    $covered = [double]$counter.covered
    $total = $missed + $covered
    
    $coveragePercent = [math]::Round(($covered / $total) * 100, 2)
    $minRequired = $WFConfig.quality_gate.min_coverage

    Write-WFLog "RESULTADO COBERTURA: $coveragePercent%" "Cyan"

    if ($coveragePercent -lt $minRequired) {
        Write-WFLog "--- QUALITY GATE FALLIDO ---" "Red"
        Write-WFLog "Cobertura actual ($coveragePercent%) es menor al mínimo requerido ($minRequired%)." "Red"
        Write-WFLog "Acción: Añade más pruebas unitarias a tus métodos críticos." "Yellow"
        exit 1
    } else {
        Write-WFLog "--- QUALITY GATE SUPERADO ---" "Green"
        Write-WFLog "Cobertura suficiente para los estándares del proyecto ($minRequired%)." "Gray"
    }
} else {
    Write-WFLog "AVISO: No se encontró reporte JaCoCo XML. Asegúrate de que el plugin esté en el pom.xml." "Yellow"
}

# 3. Mostrar reporte visual
$reportPath = "target/site/jacoco/index.html"
if (Test-Path $reportPath) {
    Write-WFLog "Abriendo reporte interactivo..." "Gray"
    # Solo abrir si estamos en modo local e interactivo
    # Invoke-Item $reportPath
}
