# init_workflow.ps1
# Setup Wizard: Instala y configura el Framework DevOps en cualquier proyecto vacío.

Clear-Host
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " 🚀 SETUP WIZARD: ANTIGRAVITY DEVOPS FRAMEWORK " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "Este asistente configurara el ecosistema CI/CD para este repositorio." -ForegroundColor Gray

$scriptPath = $PSScriptRoot
$configFile = "$scriptPath\workflow_config.json"

# 1. Chequeo de dependencias base
Write-Host "`n[1/5] Verificando dependencias del sistema..." -ForegroundColor Yellow
$gitCheck = Get-Command git -ErrorAction SilentlyContinue
if (-not $gitCheck) { Write-Host "❌ Git no esta instalado. Abortando." -ForegroundColor Red; exit }

$ghCheck = Get-Command gh -ErrorAction SilentlyContinue
if (-not $ghCheck) { Write-Host "⚠️ GitHub CLI (gh) no detectado. Algunas automatizaciones de PR requeriran instalacion posterior." -ForegroundColor DarkYellow }
else { Write-Host "✅ Git y GitHub CLI detectados." -ForegroundColor Green }

# 2. Inicializar Git si es necesario (asume que la raíz es .. respecto a WorkFlow)
$rootDir = (Get-Item $scriptPath).Parent.FullName
if (-not (Test-Path "$rootDir\.git")) {
    Write-Host "Inicializando repositorio Git en la raiz..." -ForegroundColor Gray
    Set-Location $rootDir
    git init -q
    Set-Location $scriptPath
}

# 3. Recopilar datos del proyecto
Write-Host "`n[2/5] Configuracion del Proyecto..." -ForegroundColor Yellow
$projectName = Read-Host "Nombre legible del proyecto (ej: Gestor de Inventario)"
if ([string]::IsNullOrWhiteSpace($projectName)) { $projectName = "My Project" }

$repoOwner = Read-Host "Usuario u Organizacion de GitHub (ej: bbaakkuu-svg)"
if ([string]::IsNullOrWhiteSpace($repoOwner)) { $repoOwner = "my-org" }

$repoName = Read-Host "Nombre del repositorio en GitHub (ej: gestor_inventario_backend)"
if ([string]::IsNullOrWhiteSpace($repoName)) { $repoName = "my-repo" }

# 4. Seleccionar Stack Tecnologico
Write-Host "`n[3/5] Selecciona el Stack Tecnologico Principal:" -ForegroundColor Yellow
Write-Host "[1] Java (Maven)"
Write-Host "[2] Node.js (NPM)"
Write-Host "[3] Python (Pytest/Pip)"
$stackChoice = Read-Host "Opcion (1-3)"

$lang = "java"
$formatCmd = "mvn spotless:apply -q"
$testCmd = "mvn test -q"
$covCmd = "mvn clean test jacoco:report"
$versionFile = "pom.xml"
$versionSetCmd = "mvn versions:set -DnewVersion={VERSION} -DgenerateBackupPoms=false -q"

switch ($stackChoice) {
    "2" {
        $lang = "node"
        $formatCmd = "npm run format"
        $testCmd = "npm test"
        $covCmd = "npm run test:coverage"
        $versionFile = "package.json"
        $versionSetCmd = "npm version {VERSION} --no-git-tag-version"
    }
    "3" {
        $lang = "python"
        $formatCmd = "black ."
        $testCmd = "pytest"
        $covCmd = "pytest --cov=src"
        $versionFile = "pyproject.toml"
        $versionSetCmd = "poetry version {VERSION}" 
    }
}

# 5. Generar o Actualizar workflow_config.json
Write-Host "`n[4/5] Generando workflow_config.json dinamicamente..." -ForegroundColor Yellow

$configObj = [ordered]@{
    project = [ordered]@{
        name = $projectName
        owner = $repoOwner
        repo_name = $repoName
    }
    github_settings = [ordered]@{ project_number = 1 }
    git_flow = [ordered]@{
        main_branch = "main"
        develop_branch = "develop"
        feature_prefix = "feature/"
        release_prefix = "release/"
        hotfix_prefix = "hotfix/"
    }
    tech_stack = [ordered]@{
        language = $lang
        format_command = $formatCmd
        test_command = $testCmd
        coverage_command = $covCmd
        version_file = $versionFile
        version_set_command = $versionSetCmd
    }
    quality_gate = [ordered]@{ min_coverage = 80 }
    commit_lint = [ordered]@{
        patterns = @("feat", "fix", "docs", "style", "refactor", "test", "chore")
        forbidden_words = @("crear", "nuevo", "arreglo", "corregido", "subida", "documentacion", "prueba", "error")
        min_length = 10
    }
    security_scanner = [ordered]@{
        blocked_patterns = @("password=", "secret=", "jdbc:mysql://.*:[0-9]+/", "127.0.0.1", "root", "admin123", "api_key=")
    }
    paths = [ordered]@{
        src = "src"
        tests = "tests"
        docs = "docs"
    }
}

$jsonOutput = $configObj | ConvertTo-Json -Depth 5
Set-Content -Path $configFile -Value $jsonOutput
Write-Host "Diccionario maestro guardado." -ForegroundColor Gray

# 6. Preparar Actions
Write-Host "`n[5/5] Inyectando directorios del Ecosistema..." -ForegroundColor Yellow
$workflowsDir = "$rootDir\.github\workflows"
if (-not (Test-Path $workflowsDir)) {
    New-Item -ItemType Directory -Force -Path $workflowsDir | Out-Null
    Write-Host "Carpeta de GitHub Actions creada." -ForegroundColor Gray
}

Write-Host "`n==========================================================" -ForegroundColor Green
Write-Host " ✅ SETUP WIZARD FINALIZADO CON EXITO " -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Green
Write-Host "El Framework DevOps (Agnostic Edition) ha sido instalado para: $($lang.ToUpper())" -ForegroundColor Cyan
Write-Host "Todos los scripts, bloqueos de seguridad y automatizaciones responden ahora a tu nuevo Stack."
Write-Host "Ejecuta '.\devops.ps1' en la raiz del proyecto para comenzar." -ForegroundColor Cyan
