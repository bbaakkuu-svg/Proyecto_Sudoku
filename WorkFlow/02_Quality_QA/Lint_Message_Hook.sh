#!/bin/bash
# Lint_Message_Hook.sh
# Linter de mensajes de commit para Git Hooks (v3.2)

source "$(dirname "$0")/../00_Core_Loader.sh"

MESSAGE=$1

# 1. Validar Longitud Mínima
MIN_LEN=$(jq -r '.commit_lint.min_length' "$CONFIG_PATH")
if [ ${#MESSAGE} -lt "$MIN_LEN" ]; then
    write_wf_log "ERROR: Mensaje demasiado corto (mínimo $MIN_LEN caracteres)." "Red"
    exit 1
fi

# 2. Validar Palabras Prohibidas (Español)
FORBIDDEN=$(jq -r '.commit_lint.forbidden_words[]' "$CONFIG_PATH")
for word in $FORBIDDEN; do
    if [[ "$(echo "$MESSAGE" | tr '[:upper:]' '[:lower:]')" == *"$word"* ]]; then
        write_wf_log "ERROR: Has usado una palabra no técnica o en español: '$word'." "Red"
        exit 1
    fi
done

# 3. Validar Patrón de Commits Atómicos
PATTERNS=$(jq -r '.commit_lint.patterns[]' "$CONFIG_PATH")
FOUND=0
for p in $PATTERNS; do
    if [[ "$MESSAGE" == "$p"* ]]; then
        FOUND=1
        break
    fi
done

if [ $FOUND -eq 0 ]; then
    write_wf_log "ERROR: El mensaje debe empezar con un patrón válido (feat, fix, refactor, etc)." "Red"
    exit 1
fi

exit 0
