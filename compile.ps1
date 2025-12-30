# Smart Code Snippet Manager - PowerShell Compilation Script
# Unified Edition (Single Window with CardLayout)

Write-Host ""
Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║  Smart Code Snippet Manager - Compilation Script          ║" -ForegroundColor Cyan
Write-Host "║  Unified Edition (Single Window with CardLayout)          ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

$projectPath = "e:\DSA\smart-code-snippet-manager\java\src"
$binPath = Join-Path $projectPath "bin"

# Create bin directory if it doesn't exist
if (-not (Test-Path $binPath)) {
    Write-Host "[*] Creating bin directory..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Path $binPath | Out-Null
    Write-Host "[✓] bin directory created" -ForegroundColor Green
}

# List of files to compile
$files = @(
    "com/snippetmanager/SmartCodeMain.java",
    "com/snippetmanager/module1/SnippetManagerPanel.java",
    "com/snippetmanager/module1/CodeSnippetApp.java",
    "com/snippetmanager/module3/RecommendationPanelPro.java",
    "com/snippetmanager/module3/AnalyticsDashboardPro.java",
    "com/snippetmanager/module3/TagVisualization.java",
    "com/snippetmanager/module3/MetricsDashboard.java"
)

Write-Host ""
Write-Host "[*] Compiling all Java files..." -ForegroundColor Yellow
Write-Host ""

# Build javac command
$javacArgs = @("-d", $binPath)
foreach ($file in $files) {
    $javacArgs += (Join-Path $projectPath $file)
}

# Change to project directory and compile
Push-Location $projectPath
javac @javacArgs

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Green
    Write-Host "║  ✓ Compilation Successful!                                ║" -ForegroundColor Green
    Write-Host "║                                                            ║" -ForegroundColor Green
    Write-Host "║  Compiled Files Location: bin/com/snippetmanager/         ║" -ForegroundColor Green
    Write-Host "║                                                            ║" -ForegroundColor Green
    Write-Host "║  To run the application, execute:                         ║" -ForegroundColor Green
    Write-Host "║  java -cp bin com.snippetmanager.SmartCodeMain            ║" -ForegroundColor Green
    Write-Host "║                                                            ║" -ForegroundColor Green
    Write-Host "║  Or use: .\run.ps1                                        ║" -ForegroundColor Green
    Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Green
    Write-Host ""
} else {
    Write-Host ""
    Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Red
    Write-Host "║  ✗ Compilation Failed!                                    ║" -ForegroundColor Red
    Write-Host "║                                                            ║" -ForegroundColor Red
    Write-Host "║  Check error messages above and fix issues.               ║" -ForegroundColor Red
    Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Red
    Write-Host ""
}

Pop-Location

# Display summary
Write-Host "Compilation Summary:" -ForegroundColor Cyan
Write-Host "  Project Path: $projectPath" -ForegroundColor Gray
Write-Host "  Output Path: $binPath" -ForegroundColor Gray
Write-Host "  Files Compiled: $($files.Count)" -ForegroundColor Gray
Write-Host ""
