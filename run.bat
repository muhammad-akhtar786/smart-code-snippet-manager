@echo off
REM ============================================
REM Smart Code Snippet Manager - Run Script
REM ============================================

echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║  Smart Code Snippet Manager - Launching Application       ║
echo ║  Unified Edition (Single Window with CardLayout)          ║
echo ╚════════════════════════════════════════════════════════════╝
echo.

cd /d "e:\DSA\smart-code-snippet-manager\java\src"

if not exist bin (
    echo [!] bin directory not found. Running compilation...
    call compile.bat
    goto :end
)

REM Check if classes are compiled
if not exist "bin\com\snippetmanager\SmartCodeMain.class" (
    echo [!] Classes not found. Running compilation...
    call compile.bat
    goto :end
)

echo [*] Launching Smart Code Snippet Manager...
echo.

REM Change to project root so Data folder is accessible
cd /d "e:\DSA\smart-code-snippet-manager"

REM Run Java with classpath pointing to bin directory
java -cp "java\src\bin" com.snippetmanager.SmartCodeMain

:end
pause
