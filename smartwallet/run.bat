@echo off
REM Script de lancement de l'application SmartWallet
cd /d "%~dp0"
mvn clean javafx:run
pause

