@echo off
REM Script pour forcer le rechargement des dépendances Maven dans IntelliJ IDEA

echo.
echo ========================================
echo  Correction des Dependances Maven
echo ========================================
echo.

cd /d %~dp0

REM Supprimer le cache IntelliJ
echo Suppression des caches IDE...
if exist .idea\libraries rmdir /s /q .idea\libraries 2>nul
if exist .idea\artifacts rmdir /s /q .idea\artifacts 2>nul
del /f /q .idea\modules.xml 2>nul
del /f /q .idea\compiler.xml 2>nul

echo Cache IDE supprime.
echo.

REM Créer un fichier de marqueur pour forcer la revalidation
echo Création d'un marqueur de revalidation...
echo. > pom.xml.bak
del /f /q pom.xml.bak 2>nul

echo.
echo ========================================
echo Instructions:
echo ========================================
echo 1. Dans IntelliJ IDEA, allez a: File > Reload All from Disk
echo 2. Ou pressez: Ctrl + Alt + Y (Sync Maven)
echo 3. Ou allez a: Tools > Maven > Reload Projects
echo.
echo Les dependances Spring Framework devraient etre resolues automatiquement.
echo.
pause

