@echo off
REM ============================================
REM Smart Code Snippet Manager - Compile Script
REM ============================================

echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║  Smart Code Snippet Manager - Compilation Script          ║
echo ║  Unified Edition (Single Window with CardLayout)          ║
echo ║  Compiling C++ Backends + Java Application                ║
echo ╚════════════════════════════════════════════════════════════╝
echo.

REM Compile C++ Module 1
echo [*] Compiling C++ Module1...
cd /d "e:\DSA\smart-code-snippet-manager\cpp\module1"
g++ -o app.exe main.cpp hashmap.cpp trie.cpp 2>&1
if %ERRORLEVEL% equ 0 (
    echo [✓] Module1 compiled successfully
) else (
    echo [✗] Module1 compilation failed
    pause
    exit /b 1
)

REM Compile C++ Module 3
echo [*] Compiling C++ Module3...
cd /d "e:\DSA\smart-code-snippet-manager\cpp\module3"
g++ -o app.exe main.cpp graph.cpp recommendations.cpp 2>&1
if %ERRORLEVEL% equ 0 (
    echo [✓] Module3 compiled successfully
) else (
    echo [✗] Module3 compilation failed
    pause
    exit /b 1
)

REM Compile Java Files
echo.
echo [*] Compiling Java files...
cd /d "e:\DSA\smart-code-snippet-manager\java\src"

if not exist bin (
    echo [*] Creating bin directory...
    mkdir bin
    echo [✓] bin directory created
)

echo.

javac -d bin ^
    com/snippetmanager/SmartCodeMain.java ^
    com/snippetmanager/module1/SnippetManagerPanel.java ^
    com/snippetmanager/module3/RecommendationPanelPro.java ^
    com/snippetmanager/module3/AnalyticsDashboardPro.java ^
    com/snippetmanager/module3/TagVisualization.java ^
    com/snippetmanager/module3/MetricsDashboard.java

if %ERRORLEVEL% equ 0 (
    echo.
    echo ╔════════════════════════════════════════════════════════════╗
    echo ║  ✓ All Compilation Successful!                            ║
    echo ║                                                            ║
    echo ║  C++ Backends: Compiled ✓                                 ║
    echo ║  Java Application: Compiled ✓                             ║
    echo ║                                                            ║
    echo ║  To run the application, execute:                         ║
    echo ║  java -cp bin com.snippetmanager.SmartCodeMain            ║
    echo ║                                                            ║
    echo ║  Or use run.bat script                                    ║
    echo ╚════════════════════════════════════════════════════════════╝
    echo.
) else (
    echo.
    echo ╔════════════════════════════════════════════════════════════╗
    echo ║  ✗ Java Compilation Failed!                               ║
    echo ║                                                            ║
    echo ║  Check error messages above and fix issues.               ║
    echo ╚════════════════════════════════════════════════════════════╝
    echo.
    pause
)

pause
