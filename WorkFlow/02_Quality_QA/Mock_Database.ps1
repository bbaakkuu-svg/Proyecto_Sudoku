# Mock_Database.ps1
# Levanta un entorno de base de datos MySQL en Docker para tests (RA3)
. "$PSScriptRoot\..\00_Core_Loader.ps1"

$dbConfig = $WFConfig.database_mock
$container = $dbConfig.container_name

Write-WFLog "--- GESTOR DE AMBIENTE MOCK (DOCKER) ---" "Cyan"

# 1. Comprobar si Docker está corriendo
docker version >$null 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-WFLog "ERROR: Docker no está iniciado. Por favor, abre Docker Desktop." "Red"
    exit 1
}

# 2. Verificar si el contenedor ya existe
$containerExists = docker ps -a --filter "name=$container" --format "{{.Names}}"

if ($containerExists) {
    Write-WFLog "El contenedor '$container' ya existe. Reiniciando..." "Yellow"
    docker stop $container | Out-Null
    docker start $container | Out-Null
} else {
    Write-WFLog "Creando nuevo contenedor MySQL: $container (Puerto: $($dbConfig.port_local))..." "Yellow"
    docker run --name $container `
        -e MYSQL_ROOT_PASSWORD=$($dbConfig.root_password) `
        -e MYSQL_DATABASE=$($dbConfig.db_name) `
        -p "$($dbConfig.port_local):3306" `
        -d $($dbConfig.image) | Out-Null
}

# 3. Esperar a que la DB esté lista
Write-WFLog "Esperando a que la base de datos esté lista (esto puede tardar 20s)..." "Gray"
$maxRetries = 10
$retryCount = 0
$isReady = $false

while ($retryCount -lt $maxRetries -and -not $isReady) {
    Start-Sleep -Seconds 3
    $log = docker logs $container 2>&1
    if ($log -match "ready for connections") {
        $isReady = $true
    }
    $retryCount++
    Write-WFLog "Verificando estado... ($retryCount/$maxRetries)" "Gray"
}

if ($isReady) {
    Write-WFLog "¡Base de datos MOCK lista para JUnit!" "Green"
    Write-WFLog "JDBC URL: jdbc:mysql://localhost:$($dbConfig.port_local)/$($dbConfig.db_name)" "Cyan"
} else {
    Write-WFLog "AVISO: El contenedor se inició pero no confirmó estado 'ready'. Revisa 'docker logs $container'." "Yellow"
}

Write-WFLog "RECUERDA: Usa 'docker stop $container' al terminar tus pruebas." "Yellow"
