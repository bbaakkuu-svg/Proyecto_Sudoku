# Set_Main_Protection.ps1
# Configura reglas de proteccion para la rama 'main' (RA4.i)
. "$PSScriptRoot\..\00_Core_Loader.ps1"

Write-WFLog "--- CONFIGURANDO PROTECCION DE RAMA MAIN ---" "Cyan"

$repo = "$($WFConfig.project.owner)/$($WFConfig.project.repo_name)"

# Comprobar si gh esta autenticado
gh auth status
if ($LASTEXITCODE -ne 0) {
    Write-WFLog "Error: Debes estar autenticado en GitHub CLI (gh auth login)." "Red"
    exit
}

# Configurar proteccion mediante API de GitHub
# Requiere PR para mergear y que los checks de CI pasen
$jsonBody = @'
{
  "required_status_checks": {
    "strict": true,
    "contexts": ["QA & Documentation"]
  },
  "enforce_admins": true,
  "required_pull_request_reviews": {
    "dismiss_stale_reviews": true,
    "require_code_owner_reviews": false,
    "required_approving_review_count": 1
  },
  "restrictions": null
}
'@

Write-WFLog "Aplicando reglas a $repo/branches/main..." "Yellow"
$jsonBody | gh api -X PUT "repos/$repo/branches/main/protection" --input -

if ($LASTEXITCODE -eq 0) {
    Write-WFLog "Proteccion de rama MAIN activada con exito!" "Green"
} else {
    Write-WFLog "Error al aplicar proteccion. Verifica tus permisos o si el repo es publico." "Red"
}
