@echo off
REM Script de démarrage de SmartWallet
REM Cette application nécessite Java 17+ et les composants JavaFX

echo.
echo =========================================
echo   SmartWallet - AI-Powered Wallet
echo =========================================
echo.

REM Vérifier si Maven est installé
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo Erreur: Maven n'est pas installé ou non présent dans le PATH
    echo Veuillez installer Maven et ajouter son répertoire bin au PATH
    pause
    exit /b 1
)

REM Compiler et exécuter l'application
echo Compilation et exécution de l'application...
echo.

mvn clean javafx:run

if %errorlevel% neq 0 (
    echo.
    echo Erreur lors de l'exécution de l'application
    pause
    exit /b 1
)

echo.
echo Application terminée avec succès
pause

