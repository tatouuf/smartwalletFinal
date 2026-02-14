# Script de réparation pour forcer la synchronisation des dépendances Maven
Write-Host "Nettoyage du cache IDE et resynchronisation des dépendances Maven..."

# Répertoire du projet
$projectDir = Get-Location
$ideaDir = Join-Path $projectDir ".idea"
$libDir = Join-Path $ideaDir "libraries"

# Supprimer les caches d'IDE
if (Test-Path $ideaDir) {
    Write-Host "Suppression des fichiers de cache IDE..."
    Remove-Item (Join-Path $ideaDir "artifacts") -Recurse -Force -ErrorAction SilentlyContinue
    Remove-Item (Join-Path $ideaDir "modules.xml") -Force -ErrorAction SilentlyContinue
    Remove-Item (Join-Path $ideaDir "compiler.xml") -Force -ErrorAction SilentlyContinue
    Write-Host "Cache IDE nettoyé."
}

# Vérifier les dépendances en cache
$m2 = "$env:USERPROFILE\.m2\repository"
if (Test-Path $m2) {
    Write-Host "Dépendances Maven trouvées en cache local."

    # Afficher les version de Spring trouvées
    $springPath = Join-Path $m2 "org\springframework\boot"
    if (Test-Path $springPath) {
        Write-Host "Spring Boot trouvé dans le cache:"
        Get-ChildItem $springPath | ForEach-Object { Write-Host "  - $_" }
    }
}

Write-Host "Réparation terminée. Veuillez recharger le projet dans IDE."
Write-Host "1. Utilisez File > Reload All from Disk dans l'IDE"
Write-Host "2. Ou utilisez Tools > Maven > Reload Projects"
Write-Host "3. Ou appuyez sur Ctrl+Shift+O pour recharger les dépendances"

