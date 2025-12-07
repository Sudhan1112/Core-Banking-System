$ErrorActionPreference = "Stop"
$MvnCmd = Join-Path $PSScriptRoot ".mvn-portable/bin/mvn.cmd"
if (-not (Test-Path $MvnCmd)) {
    Write-Host "Maven not found at $MvnCmd"
    exit 1
}
Write-Host "Running build..."
& $MvnCmd clean package -DskipTests *>&1 | Set-Content "build.log"
Write-Host "Build complete. Check build.log"
