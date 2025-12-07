# Check for .env file and load it
if (Test-Path -Path .env) {
    Get-Content .env | Where-Object { $_ -match '=' -and -not $_.Trim().StartsWith('#') } | ForEach-Object {
        $key, $value = $_ -split '=', 2
        [Environment]::SetEnvironmentVariable($key.Trim(), $value.Trim(), "Process")
    }
}

# Check for Maven
$MvnCmd = "mvn"
if (-not (Get-Command "mvn" -ErrorAction SilentlyContinue)) {
    $LocalMvn = Join-Path $PSScriptRoot ".mvn-portable/bin/mvn.cmd"
    if (Test-Path $LocalMvn) {
        $MvnCmd = $LocalMvn
    } else {
        Write-Host "Maven not found in PATH. Attempting to install portable Maven..."
        try {
            & "$PSScriptRoot/setup-maven.ps1"
            if (Test-Path $LocalMvn) {
                $MvnCmd = $LocalMvn
            } else {
                Write-Host "Failed to install Maven. Please install it manually."
                exit 1
            }
        } catch {
            Write-Host "Error running setup script: $_"
            exit 1
        }
    }
}

Write-Host "Using Maven: $MvnCmd"
& $MvnCmd test
