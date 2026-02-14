# Script PowerShell pour lancer SmartWallet
# ==========================================

Write-Host ""
Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║                    SMARTWALLET - v1.0                     ║" -ForegroundColor Cyan
Write-Host "║          Gestion Budgetaire Intelligente                  ║" -ForegroundColor Cyan
Write-Host "║                                                            ║" -ForegroundColor Cyan
Write-Host "║       Application lancée avec interface graphique!         ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Changer de répertoire
Set-Location "C:\Users\lolil\smartwalletFinal\smartwallet"

Write-Host "📦 Vérification du JAR..." -ForegroundColor Yellow
if (Test-Path "target\smartwallet-1.0-SNAPSHOT.jar") {
    Write-Host "✅ JAR trouvé!" -ForegroundColor Green
} else {
    Write-Host "❌ JAR non trouvé! Compilation nécessaire..." -ForegroundColor Red
    Write-Host "Lancement de la compilation..." -ForegroundColor Yellow
    & .\mvnw.cmd package -DskipTests
}

Write-Host ""
Write-Host "🚀 Lancement de l'application..." -ForegroundColor Green
Write-Host "Veuillez patienter (cela peut prendre quelques secondes)..." -ForegroundColor Yellow
Write-Host ""

# Lancer l'application
& java --enable-native-access=ALL-UNNAMED -jar target\smartwallet-1.0-SNAPSHOT.jar

Write-Host ""
Write-Host "✅ Application fermée" -ForegroundColor Green
Write-Host "Merci d'avoir utilisé SmartWallet!" -ForegroundColor Cyan
Write-Host ""

