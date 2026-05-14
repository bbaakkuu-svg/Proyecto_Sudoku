# Scan_Secrets.ps1
# Detector de Secretos y Fugas de Información (RA4.i)
. "$PSScriptRoot\..\00_Core_Loader.ps1"

Write-WFLog "--- ESCÁNER DE SEGURIDAD (SECRET SCAN) ---" "Cyan"

$foundSecrets = $false

# 1. Intento de uso de Gitleaks (Nivel Profesional)
gitleaks version >$null 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-WFLog "Ejecutando Gitleaks (Professional Scan)..." "Yellow"
    gitleaks detect --source . --verbose --redact
    if ($LASTEXITCODE -ne 0) { $foundSecrets = $true }
} else {
    # 2. Fallback: Escáner Ligero por Regex (PowerShell)
    Write-WFLog "Gitleaks no detectado. Usando Escáner de Emergencia (Regex)..." "Gray"
    
    $patterns = @{
        "AWS Key"         = "AKIA[0-9A-Z]{16}"
        "Generic Secret"  = "secret[_-]?key|api[_-]?key|password|passwd"
        "JDBC Password"   = "password\s*=\s*['\"].+['\"]"
        "Private Key"     = "-----BEGIN RSA PRIVATE KEY-----"
        "Supabase Key"    = "sbp_[a-zA-Z0-9]{40}"
    }

    $filesToScan = Get-ChildItem -Recurse -File -Exclude "*.ps1", "*.sh", "*.md", ".git*", "target*"

    foreach ($file in $filesToScan) {
        $content = Get-Content $file.FullName -Raw
        foreach ($name in $patterns.Keys) {
            $regex = $patterns[$name]
            if ($content -match $regex) {
                Write-WFLog "[ALERTA] Posible $name detectado en: $($file.Name)" "Red"
                $foundSecrets = $true
            }
        }
    }
}

if ($foundSecrets) {
    Write-WFLog "--- SEGURIDAD COMPROMETIDA ---" "Red"
    Write-WFLog "Se han detectado posibles credenciales. Limpia el código antes de subirlo." "Yellow"
    exit 1
} else {
    Write-WFLog "Escaneo de seguridad limpio. No se detectaron secretos obvios." "Green"
    exit 0
}
