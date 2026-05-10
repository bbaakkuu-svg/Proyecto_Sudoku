#!/bin/bash
# 03_Nueva_Rama.sh
# Gestor de ramas GitFlow en Bash (v3.1)

# Cargar entorno
source "$(dirname "$0")/00_Core_Loader.sh"

write_wf_log "--- GESTOR DE RAMAS GITFLOW (BASH) ---" "Cyan"

# Comprobar cambios locales
if [ -n "$(git status --porcelain)" ]; then
    write_wf_log "AVISO: Tienes cambios sin commitear." "Yellow"
fi

echo "Tipo de rama? (1: feature, 2: release, 3: hotfix)"
read -r tipo
echo "Nombre descriptivo (técnico):"
read -r nombre

if [[ $nombre =~ \  ]]; then
    write_wf_log "ERROR: El nombre no debe contener espacios." "Red"
    exit 1
fi

case $tipo in
    1)
        git checkout "$BRANCH_DEV" && git pull origin "$BRANCH_DEV"
        PREFIX=$(jq -r '.git_flow.feature_prefix' "$CONFIG_PATH")
        ;;
    2)
        git checkout "$BRANCH_DEV"
        PREFIX=$(jq -r '.git_flow.release_prefix' "$CONFIG_PATH")
        ;;
    3)
        git checkout "$BRANCH_MAIN" && git pull origin "$BRANCH_MAIN"
        PREFIX=$(jq -r '.git_flow.hotfix_prefix' "$CONFIG_PATH")
        ;;
    *)
        write_wf_log "Tipo no válido." "Red"
        exit 1
        ;;
esac

BRANCH_NAME="${PREFIX}${nombre}"

# Verificar si existe
if git branch --list "$BRANCH_NAME" | grep -q "$BRANCH_NAME"; then
    write_wf_log "ERROR: La rama '$BRANCH_NAME' ya existe." "Red"
    exit 1
fi

write_wf_log "--- CREANDO RAMA: $BRANCH_NAME ---" "Cyan"
git checkout -b "$BRANCH_NAME"
write_wf_log "Rama '$BRANCH_NAME' creada correctamente." "Green"
