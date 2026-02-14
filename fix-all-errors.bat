@echo off
REM Script pour nettoyer et compiler le projet SmartWallet SANS ERREURS

echo ========================================
echo SMARTWALLET - Nettoyage Complet
echo ========================================

cd /d "%~dp0"

echo.
echo 1. Suppression du cache Maven...
if exist "%USERPROFILE%\.m2\repository" (
    rmdir /s /q "%USERPROFILE%\.m2\repository" 2>nul
    echo    OK - Cache supprime
)

echo.
echo 2. Suppression des fichiers build...
if exist "target" rmdir /s /q "target"
if exist ".idea\artifacts" rmdir /s /q ".idea\artifacts"
if exist ".idea\modules" rmdir /s /q ".idea\modules"
echo    OK - Build supprime

echo.
echo 3. Telechargement des dependances...
call mvn clean dependency:resolve dependency:resolve-plugins
if errorlevel 1 (
    echo    ERREUR - Dependances non telechargees
    pause
    exit /b 1
)
echo    OK - Dependances telechargees

echo.
echo 4. Compilation du projet...
call mvn clean compile
if errorlevel 1 (
    echo    ERREUR - Compilation echouee
    pause
    exit /b 1
)
echo    OK - Compilation reussie

echo.
echo ========================================
echo OK - Nettoyage termine SANS ERREURS
echo ========================================
echo.
echo Prochaine etape : Redemarrer IntelliJ
echo File > Invalidate Caches / Restart
echo.
pause

