#!/bin/bash
# 08_Publicar_Release.sh
# Release y Changelog en Bash (v3.1)

source "$(dirname "$0")/00_Core_Loader.sh"

write_wf_log "--- GESTOR DE LANZAMIENTOS (BASH) ---" "Cyan"

echo "Número de versión (ej: 1.1.0):"
read -r version
if [ -z "$version" ]; then exit 1; fi

TAG_NAME="v$version"
CHANGELOG_FILE="CHANGELOG.md"

# 1. Notas de PRs
write_wf_log "Generando notas de lanzamiento..." "Yellow"
RELEASE_NOTES=$(gh pr list --state merged --limit 10 --json title,number --jq '.[] | "- " + .title + " (#" + (.number|tostring) + ")"')

# 2. Update Changelog
DATE=$(date +%Y-%m-%d)
NEW_ENTRY="## [$version] - $DATE\n### Changed & Fixed\n$RELEASE_NOTES\n"

if [ -f "$CHANGELOG_FILE" ]; then
    # Crear archivo temporal para evitar incompatibilidades de 'sed -i' entre Mac y Linux
    echo -e "# Changelog\n\n$NEW_ENTRY" > "${CHANGELOG_FILE}.tmp"
    tail -n +2 "$CHANGELOG_FILE" >> "${CHANGELOG_FILE}.tmp"
    mv "${CHANGELOG_FILE}.tmp" "$CHANGELOG_FILE"
else
    echo -e "# Changelog\n\n$NEW_ENTRY" > "$CHANGELOG_FILE"
fi

# 3. GitFlow
git add .
git commit -m "chore(release): $TAG_NAME [skip ci]"
git tag -a "$TAG_NAME" -m "Release $TAG_NAME"

git checkout "$BRANCH_MAIN"
git merge "$BRANCH_DEV"
git push origin "$BRANCH_MAIN" "$BRANCH_DEV" --tags

# 4. GitHub Release
gh release create "$TAG_NAME" --title "$PROJECT_NAME $TAG_NAME" --notes "$RELEASE_NOTES"

write_wf_log "¡Lanzamiento $TAG_NAME completado!" "Green"
git checkout "$BRANCH_DEV"
