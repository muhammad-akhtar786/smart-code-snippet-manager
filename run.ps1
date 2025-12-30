# Smart Code Snippet Manager - PowerShell Run Script
# Unified Edition (Single Window with CardLayout)

Write-Host ""
Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║  Smart Code Snippet Manager - Launching Application       ║" -ForegroundColor Cyan
Write-Host "║  Unified Edition (Single Window with CardLayout)          ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

$projectPath = "e:\DSA\smart-code-snippet-manager\java\src"
$binPath = Join-Path $projectPath "bin"
$mainClass = "com.snippetmanager.SmartCodeMain"

# Check if bin directory exists
if (-not (Test-Path $binPath)) {
    Write-Host "[!] bin directory not found. Running compilation..." -ForegroundColor Yellow
    & "$PSScriptRoot\compile.ps1"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Compilation failed. Cannot proceed." -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
}

# Check if main class is compiled
$mainClassPath = Join-Path $binPath "com\snippetmanager\SmartCodeMain.class"
if (-not (Test-Path $mainClassPath)) {
    Write-Host "[!] Main class not found. Running compilation..." -ForegroundColor Yellow
    & "$PSScriptRoot\compile.ps1"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Compilation failed. Cannot proceed." -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
}

Write-Host "[*] Launching Smart Code Snippet Manager..." -ForegroundColor Green
Write-Host ""
Write-Host "Features:" -ForegroundColor Cyan
Write-Host "  ✓ Single Window (No popups)" -ForegroundColor Green
Write-Host "  ✓ Left Vertical Menu" -ForegroundColor Green
Write-Host "  ✓ Fast Module Switching (CardLayout)" -ForegroundColor Green
Write-Host "  ✓ C++ Backend Integration" -ForegroundColor Green
Write-Host "  ✓ Module 1: Snippet Manager" -ForegroundColor Green
Write-Host "  ✓ Module 3: Recommendations & Analytics" -ForegroundColor Green
Write-Host ""

# Change to bin directory and run
Push-Location $binPath
java $mainClass
$exitCode = $LASTEXITCODE
Pop-Location

if ($exitCode -ne 0) {
    Write-Host ""
    Write-Host "Application exited with code: $exitCode" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Application closed." -ForegroundColor Cyan
