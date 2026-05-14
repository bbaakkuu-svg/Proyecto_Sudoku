#!/bin/bash
# 00_Core_Loader.sh
# Engine de carga de configuración para Bash (Linux/macOS) v3.1

# Localizar la raíz de la carpeta WorkFlow independientemente de dónde se ejecute el script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
CONFIG_PATH="$SCRIPT_DIR/workflow_config.json"

# Función de log profesional
write_wf_log() {
    local message=$1
    local color=$2
    case $color in
        "Red")    echo -e "\033[0;31m[WorkFlow] $message\033[0m" ;;
        "Green")  echo -e "\033[0;32m[WorkFlow] $message\033[0m" ;;
        "Yellow") echo -e "\033[0;33m[WorkFlow] $message\033[0m" ;;
        "Cyan")   echo -e "\033[0;36m[WorkFlow] $message\033[0m" ;;
        *)        echo "[WorkFlow] $message" ;;
    esac
}

# Verificar dependencia 'jq'
if ! command -v jq &> /dev/null; then
    write_wf_log "ERROR: 'jq' no está instalado. Es necesario para procesar JSON en Bash." "Red"
    exit 1
fi

if [ ! -f "$CONFIG_PATH" ]; then
    write_wf_log "ERROR: No se encuentra workflow_config.json en $(dirname "$0")" "Red"
    exit 1
fi

# Cargar variables críticas desde JSON usando jq
REPO_OWNER=$(jq -r '.project.owner' "$CONFIG_PATH")
REPO_NAME=$(jq -r '.project.repo_name' "$CONFIG_PATH")
BRANCH_MAIN=$(jq -r '.git_flow.main_branch' "$CONFIG_PATH")
BRANCH_DEV=$(jq -r '.git_flow.develop_branch' "$CONFIG_PATH")
PROJECT_NUM=$(jq -r '.github_settings.project_number' "$CONFIG_PATH")
PROJECT_NAME=$(jq -r '.project.name' "$CONFIG_PATH")

write_wf_log "Entorno Bash cargado para: $PROJECT_NAME" "Green"
