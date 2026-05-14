#!/bin/bash
# Scan_Secrets.sh
# Detector de Secretos en Bash (v3.2)

source "$(dirname "$0")/../00_Core_Loader.sh"

write_wf_log "--- ESCÁNER DE SEGURIDAD (BASH) ---" "Cyan"

# 1. Gitleaks
if command -v gitleaks &> /dev/null; then
    write_wf_log "Ejecutando Gitleaks..." "Yellow"
    gitleaks detect --source . --verbose
    exit $?
fi

# 2. Fallback (Grep)
write_wf_log "Gitleaks no detectado. Usando Grep (Basic Scan)..." "Gray"

# Patrones críticos
PATTERNS=("AKIA[0-9A-Z]\{16\}" "api_key" "password" "sbp_[a-zA-Z0-9]\{40\}")
FOUND=0

for p in "${PATTERNS[@]}"; do
    # Buscar en archivos, excluyendo WorkFlow y carpetas ocultas
    if grep -rEi "$p" . --exclude-dir={.git,target,WorkFlow} &> /dev/null; then
        write_wf_log "[ALERTA] Patrón sospechoso detectado: $p" "Red"
        FOUND=1
    fi
done

if [ $FOUND -eq 1 ]; then
    write_wf_log "--- RIESGO DE SEGURIDAD DETECTADO ---" "Red"
    exit 1
fi

write_wf_log "Seguridad validada (Grep)." "Green"
exit 0
