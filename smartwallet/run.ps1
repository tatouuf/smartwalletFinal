# Script de démarrage de SmartWallet pour PowerShell
# Cette application nécessite Java 17+ et les composants JavaFX

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "   SmartWallet - AI-Powered Wallet" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# Vérifier si Maven est installé
$mvnPath = Get-Command mvn -ErrorAction SilentlyContinue

if (-not $mvnPath) {
    Write-Host "Erreur: Maven n'est pas installé ou non présent dans le PATH" -ForegroundColor Red
    Write-Host "Veuillez installer Maven et ajouter son répertoire bin au PATH" -ForegroundColor Red
    exit 1
}

# Compiler et exécuter l'application
Write-Host "Compilation et exécution de l'application..." -ForegroundColor Yellow
Write-Host ""

cd $PSScriptRoot
mvn clean javafx:run

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "Erreur lors de l'exécution de l'application" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Application terminée avec succès" -ForegroundColor Green

