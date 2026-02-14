@echo off
REM Script to sync Maven dependencies for IntelliJ
setlocal enabledelayedexpansion

echo Synchronizing Maven dependencies...
cd /d %~dp0

REM Try using Maven wrapper if available
if exist mvnw.cmd (
    echo Using Maven wrapper...
    call mvnw.cmd dependency:resolve
    call mvnw.cmd dependency:tree
) else (
    echo Maven wrapper not available, attempting to use mvn command...
    if exist "C:\Program Files\Apache Maven\bin\mvn.cmd" (
        call "C:\Program Files\Apache Maven\bin\mvn.cmd" dependency:resolve
        call "C:\Program Files\Apache Maven\bin\mvn.cmd" dependency:tree
    ) else (
        echo Maven not found. Please ensure Maven is installed and in PATH.
        exit /b 1
    )
)

echo Dependency synchronization complete.
pause

