# Script PowerShell pour forcer l'utilisation d'un JDK 21 et compiler le projet Maven
# - Recherche des installations communes d'Adoptium/Temurin JDK 21
# - Si trouvé, définit JAVA_HOME et met à jour PATH pour la session
# - Lance : .\mvnw.cmd -DskipTests clean compile

param(
    [string]$Jdk21Path
)

function Set-JavaHomeAndBuild($path) {
    Write-Host "Utilisation de JDK 21 trouvé dans : $path"
    $env:JAVA_HOME = $path
    $env:Path = "$($env:JAVA_HOME)\bin;$env:Path"

    Write-Host "java -version:"; java -version
    Write-Host "javac -version:"; javac -version

    Write-Host "Lancement de la compilation Maven (wrapper)..."
    & .\mvnw.cmd -DskipTests clean compile
}

# Emplacements courants (Windows) pour Temurin/Adoptium JDK 21
$commonPaths = @(
    'C:\Program Files\Eclipse Adoptium\jdk-21.0.7.1-hotspot',
    'C:\Program Files\Eclipse Adoptium\jdk-21.0.7-hotspot',
    'C:\Program Files\AdoptOpenJDK\jdk-21',
    'C:\Program Files\Java\jdk-21'
)

if ($Jdk21Path) {
    if (Test-Path $Jdk21Path) {
        Set-JavaHomeAndBuild $Jdk21Path
        exit $LASTEXITCODE
    } else {
        Write-Host "Le chemin fourni n'existe pas : $Jdk21Path" -ForegroundColor Red
        exit 1
    }
}

foreach ($p in $commonPaths) {
    if (Test-Path $p) {
        Set-JavaHomeAndBuild $p
        exit $LASTEXITCODE
    }
}

# Si non trouvé automatiquement, demander à l'utilisateur
Write-Host "JDK 21 non trouvé automatiquement. Veuillez entrer le chemin vers votre JDK 21 (ex: C:\\Program Files\\Eclipse Adoptium\\jdk-21...):"
$manual = Read-Host -Prompt 'Chemin JDK 21'
if (Test-Path $manual) {
    Set-JavaHomeAndBuild $manual
} else {
    Write-Host "Chemin invalide. Abandon." -ForegroundColor Red
    exit 1
}

