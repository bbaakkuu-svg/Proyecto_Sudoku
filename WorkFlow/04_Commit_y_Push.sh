#!/bin/bash
# 04_Commit_y_Push.sh
# Commit Linter y Push en Bash (v3.1)

source "$(dirname "$0")/00_Core_Loader.sh"

write_wf_log "--- COMMIT LINTER & PUSH (BASH) ---" "Cyan"

echo "Commit Message (English):"
read -r mensaje

# 1. Longitud
MIN_LEN=$(jq -r '.commit_lint.min_length' "$CONFIG_PATH")
if [ ${#mensaje} -lt "$MIN_LEN" ]; then
    write_wf_log "ERROR: Mensaje demasiado corto." "Red"
    exit 1
fi

# 2. Palabras prohibidas
FORBIDDEN=$(jq -r '.commit_lint.forbidden_words[]' "$CONFIG_PATH")
for word in $FORBIDDEN; do
    if [[ "$mensaje" == *"$word"* ]]; then
        write_wf_log "ERROR: Palabra prohibida detectada: $word" "Red"
        exit 1
    fi
done

# 3. Patrones
PATTERNS=$(jq -r '.commit_lint.patterns[]' "$CONFIG_PATH")
FOUND=0
for p in $PATTERNS; do
    if [[ "$mensaje" == "$p"* ]]; then
        FOUND=1
        break
    fi
done

if [ $FOUND -eq 0 ]; then
    write_wf_log "ERROR: El mensaje no sigue el estándar feat/fix/etc." "Red"
    exit 1
fi

write_wf_log "--- LINT PASADO ---" "Green"
git add .
git commit -m "$mensaje"
BRANCH=$(git branch --show-current)
git push origin "$BRANCH"
