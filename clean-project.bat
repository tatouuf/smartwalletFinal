@echo off
REM Script pour nettoyer complètement IntelliJ et Maven sur Windows

echo ======================================
echo Nettoyage complet du projet SmartWallet
echo ======================================

REM 1. Supprimer le cache Maven
echo 1. Suppression du cache Maven...
if exist "%USERPROFILE%\.m2\repository" (
    rmdir /s /q "%USERPROFILE%\.m2\repository"
    echo    OK - Cache Maven supprime
) else (
    echo    Info - Cache Maven non trouve
)

REM 2. Nettoyer le build Maven
echo 2. Nettoyage du build Maven...
cd /d "%~dp0"
call mvn clean
echo    OK - Build nettoye

REM 3. Telecharger les dependances
echo 3. Telechargement des dependances...
call mvn -U dependency:resolve-plugins dependency:resolve
echo    OK - Dependances telechargees

REM 4. Compiler le projet
echo 4. Compilation du projet...
call mvn compile
if errorlevel 0 (
    echo    OK - Compilation reussie
) else (
    echo    ERREUR - Compilation echouee
    pause
    exit /b 1
)

echo.
echo ======================================
echo OK - Nettoyage termine
echo ======================================
echo Prochaine etape: Recharger le projet dans l'IDE
echo.
pause

