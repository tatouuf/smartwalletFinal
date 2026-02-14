@echo off
REM Lancer l'application SmartWallet avec l'interface graphique JavaFX
REM ====================================================================

echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║                    SMARTWALLET - v1.0                     ║
echo ║          Gestion Budgetaire Intelligente                  ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo Lancement de l'application en cours...
echo.

cd C:\Users\lolil\smartwalletFinal\smartwallet

REM Ajouter les arguments JavaFX et lancer l'application
set JAVA_OPTS=--enable-native-access=ALL-UNNAMED

REM Lancer le JAR compilé avec Spring Boot
java %JAVA_OPTS% -jar target\smartwallet-1.0-SNAPSHOT.jar

pause

