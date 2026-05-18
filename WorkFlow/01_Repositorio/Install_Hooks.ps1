# Install_Hooks.ps1
# Instala automatismos de Git (Hooks) para validación automática (RA3, RA4)
. "$PSScriptRoot\..\00_Core_Loader.ps1"

Write-WFLog "--- INSTALADOR DE GIT HOOKS (v3.2) ---" "Cyan"

$hooksDir = Join-Path (Get-Location) ".git\hooks"

if (-not (Test-Path $hooksDir)) {
    Write-WFLog "ERROR: No se encontró la carpeta .git. ¿Has inicializado el repositorio?" "Red"
    exit 1
}

# 1. Hook: pre-commit (Ejecuta Tests, Quality Gate y Secret Scan)
$preCommitContent = @"
#!/bin/bash
# Hook para validar seguridad y calidad antes de commitear
echo "[WorkFlow Hook] Ejecutando Escaneo de Seguridad..."
./WorkFlow/04_Seguridad/Scan_Secrets.sh
if [ $? -ne 0 ]; then
    echo -e "\033[0;31m[ERROR] Se detectaron posibles secretos (API Keys/Passwords). Commit abortado.\033[0m"
    exit 1
fi

echo "[WorkFlow Hook] Ejecutando Quality Gate..."
./WorkFlow/02_Quality_QA/Run_Tests.sh
"@

# 2. Hook: commit-msg (Valida el mensaje y el inglés técnico)
$commitMsgContent = @"
#!/bin/bash
# Hook para validar el mensaje del commit
MSG_FILE=\$1
MESSAGE=\$(cat \$MSG_FILE)

echo "[WorkFlow Hook] Validando mensaje de commit..."
# Llamamos a un script de validación especializado
./WorkFlow/02_Quality_QA/Lint_Message_Hook.sh "\$MESSAGE"
if [ $? -ne 0 ]; then
    echo -e "\033[0;31m[ERROR] Formato de commit inválido o no profesional. Commit abortado.\033[0m"
    exit 1
fi
"@

# Escribir archivos
$preCommitContent | Set-Content (Join-Path $hooksDir "pre-commit") -NoNewline
$commitMsgContent | Set-Content (Join-Path $hooksDir "commit-msg") -NoNewline

Write-WFLog "Hooks instalados correctamente en $hooksDir" "Green"
Write-WFLog "A partir de ahora, cada 'git commit' será validado automáticamente." "Yellow"
