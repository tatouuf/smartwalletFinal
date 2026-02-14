@echo off
REM Script pour forcer le reindexation complet du projet dans IntelliJ IDEA

setlocal enabledelayedexpansion

echo.
echo ================================================
echo  Reindexation Complete du Projet
echo ================================================
echo.

cd /d %~dp0

REM Sauvegarder les fichiers importants
echo Sauvegarde des configurations importantes...
if exist .idea\encodings.xml copy .idea\encodings.xml encodings.xml.bak >nul
if exist .idea\vcs.xml copy .idea\vcs.xml vcs.xml.bak >nul

REM Supprimer les caches
echo Suppression des caches IDE...
if exist .idea\libraries (
    rmdir /s /q .idea\libraries 2>nul
    echo  - Cache libraries supprime
)
if exist .idea\caches (
    rmdir /s /q .idea\caches 2>nul
    echo  - Cache caches supprime
)

del /f /q .idea\modules.xml 2>nul & echo  - modules.xml supprime
del /f /q .idea\compiler.xml 2>nul & echo  - compiler.xml supprime
del /f /q .idea\artifacts 2>nul & echo  - artifacts supprime
del /f /q .idea\runConfigurations 2>nul & echo  - runConfigurations supprime

echo.
echo Restauration des configurations...
if exist encodings.xml.bak (
    copy encodings.xml.bak .idea\encodings.xml >nul
    del /f /q encodings.xml.bak
)
if exist vcs.xml.bak (
    copy vcs.xml.bak .idea\vcs.xml >nul
    del /f /q vcs.xml.bak
)

echo.
echo ================================================
echo SUCCESS!
echo ================================================
echo.
echo Le cache IDE a ete completement efface.
echo Maintenant:
echo  1. Fermez IntelliJ IDEA completement
echo  2. Rouvrez le projet
echo  3. Attendez le reindexation automatique (quelques minutes)
echo  4. Les dependances Spring devraient etre resolues
echo.
echo Alternative - Si le reindexage automatique ne fonctionne pas:
echo  1. Dans IntelliJ: File > Invalidate Caches > Invalidate and Restart
echo  2. Ou: Tools > Maven > Reload Projects (Ctrl+Shift+I)
echo.
pause

