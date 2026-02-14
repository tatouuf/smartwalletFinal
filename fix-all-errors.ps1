#!/usr/bin/env pwsh
# Script PowerShell pour nettoyer complètement et compiler SmartWallet SANS ERREURS

Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "SmartWallet - Nettoyage Complet et Compilation" -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

$projectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectDir

# 1. Supprimer le cache Maven
Write-Host "1. Suppression du cache Maven..." -ForegroundColor Yellow
$mavenCache = "$env:USERPROFILE\.m2\repository"
if (Test-Path $mavenCache) {
    Remove-Item -Path $mavenCache -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "   ✓ Cache Maven supprimé" -ForegroundColor Green
} else {
    Write-Host "   ℹ Cache Maven non trouvé" -ForegroundColor Gray
}

# 2. Supprimer les fichiers de build locaux
Write-Host ""
Write-Host "2. Suppression des fichiers de build..." -ForegroundColor Yellow
if (Test-Path "target") {
    Remove-Item -Path "target" -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "   ✓ Dossier target supprimé" -ForegroundColor Green
}

if (Test-Path ".idea\artifacts") {
    Remove-Item -Path ".idea\artifacts" -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "   ✓ Dossier .idea\artifacts supprimé" -ForegroundColor Green
}

# 3. Nettoyer Maven
Write-Host ""
Write-Host "3. Nettoyage Maven (mvn clean)..." -ForegroundColor Yellow
& mvn clean -q
if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✓ Nettoyage réussi" -ForegroundColor Green
} else {
    Write-Host "   ✗ Erreur lors du nettoyage" -ForegroundColor Red
}

# 4. Forcer le téléchargement des dépendances
Write-Host ""
Write-Host "4. Téléchargement des dépendances (forçé)..." -ForegroundColor Yellow
& mvn -U dependency:resolve dependency:resolve-plugins -q
if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✓ Dépendances téléchargées" -ForegroundColor Green
} else {
    Write-Host "   ✗ Erreur lors du téléchargement" -ForegroundColor Red
}

# 5. Compiler le projet
Write-Host ""
Write-Host "5. Compilation du projet (mvn compile)..." -ForegroundColor Yellow
& mvn clean compile -q
if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✓ Compilation réussie" -ForegroundColor Green
} else {
    Write-Host "   ✗ Erreur lors de la compilation" -ForegroundColor Red
    Write-Host ""
    Write-Host "Détails de l'erreur :" -ForegroundColor Red
    & mvn clean compile
    Read-Host "Appuyez sur Entrée pour quitter"
    exit 1
}

# 6. Afficher le résumé
Write-Host ""
Write-Host "===============================================" -ForegroundColor Green
Write-Host "✓ Nettoyage et compilation réussis SANS ERREURS" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green
Write-Host ""
Write-Host "Prochaines étapes :" -ForegroundColor Cyan
Write-Host "1. Redémarrer IntelliJ IDEA"
Write-Host "2. File > Invalidate Caches / Restart"
Write-Host "3. Cliquer 'Invalidate and Restart'"
Write-Host "4. Les erreurs devraient disparaître"
Write-Host ""
Write-Host "Après redémarrage :" -ForegroundColor Cyan
Write-Host "$ mvn spring-boot:run"
Write-Host ""
Read-Host "Appuyez sur Entrée pour quitter"

