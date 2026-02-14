@echo off
REM Lancer l'application SmartWallet avec logs dans le terminal
REM ============================================================

echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║                    SMARTWALLET - v1.0                     ║
echo ║          Gestion Budgetaire Intelligente                  ║
echo ║                                                            ║
echo ║  L'application JavaFX s'affiche dans une fenetre           ║
echo ║  L'interface graphique est maintenant visuelle!            ║
echo ║                                                            ║
echo ╚════════════════════════════════════════════════════════════╝
echo.

cd C:\Users\lolil\smartwalletFinal\smartwallet

echo Lancement de l'application SmartWallet en cours...
echo Veuillez patienter (cela peut prendre quelques secondes)...
echo.

REM Lancer le JAR compilé
java --enable-native-access=ALL-UNNAMED -jar target\smartwallet-1.0-SNAPSHOT.jar

echo.
echo Merci d'avoir utilise SmartWallet!
echo.

pause

