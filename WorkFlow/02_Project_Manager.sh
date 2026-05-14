#!/bin/bash
# 02_Project_Manager.sh
# Gestor de GitHub Projects V2 en Bash (v5.0)

source "$(dirname "$0")/00_Core_Loader.sh"

ACTION=$1

show_help() {
    write_wf_log "Uso: ./02_Project_Manager.sh [init | sync | meta | bulk-edit]" "Cyan"
}

if [ -z "$ACTION" ]; then show_help; exit 1; fi

OWNER=$(jq -r '.project.owner' "$CONFIG_PATH")
PROJ_NUM=$(jq -r '.github_settings.project_number' "$CONFIG_PATH")
REPO="$(jq -r '.project.owner' "$CONFIG_PATH")/$(jq -r '.project.repo_name' "$CONFIG_PATH")"

case $ACTION in
    "init")
        write_wf_log "Iniciando creación de proyecto..." "Yellow"
        echo "Título:"
        read -r TITLE
        gh project create --owner "$OWNER" --title "$TITLE"
        ;;
    "sync")
        write_wf_log "Sincronizando Items masivamente..." "Yellow"
        # Obtener URLs de issues/PRs abiertos
        ITEMS=$(gh api -X GET search/issues -f q="repo:$REPO is:open" --jq '.items[].html_url')
        for URL in $ITEMS; do
            write_wf_log "Agregando $URL..." "Gray"
            gh project item-add "$PROJ_NUM" --owner "$OWNER" --url "$URL" > /dev/null
        done
        write_wf_log "Sync completado." "Green"
        ;;
    "meta")
        echo "Nueva descripción:"
        read -r DESC
        gh project edit "$PROJ_NUM" --owner "$OWNER" --description "$DESC"
        ;;
    "bulk-edit")
        write_wf_log "Edición masiva de campo..." "Yellow"
        echo "Campo (ej: Status):"
        read -r FIELD
        echo "Valor (ej: Done):"
        read -r VALUE
        
        # Obtener IDs de items
        IDS=$(gh project item-list "$PROJ_NUM" --owner "$OWNER" --format json | jq -r '.[].id')
        for ID in $IDS; do
            write_wf_log "Editando $ID..." "Gray"
            gh project item-edit --id "$ID" --project-number "$PROJ_NUM" --owner "$OWNER" --field "$FIELD" --single-select-option "$VALUE" > /dev/null
        done
        ;;
    *)
        show_help
        ;;
esac
