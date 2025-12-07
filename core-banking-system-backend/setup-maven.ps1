$ErrorActionPreference = "Stop"
$MavenVersion = "3.9.6"
$MavenUrl = "https://archive.apache.org/dist/maven/maven-3/$MavenVersion/binaries/apache-maven-$MavenVersion-bin.zip"
$InstallDir = Join-Path $PSScriptRoot ".mvn-portable"
$ZipFile = Join-Path $PSScriptRoot "maven.zip"

if (Test-Path $InstallDir) {
    Write-Host "Maven already installed in $InstallDir"
    exit 0
}

Write-Host "Downloading Maven $MavenVersion..."
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
Invoke-WebRequest -Uri $MavenUrl -OutFile $ZipFile

Write-Host "Extracting Maven..."
Expand-Archive -Path $ZipFile -DestinationPath $PSScriptRoot -Force

# Rename extracted folder to standard name
$ExtractedDir = Join-Path $PSScriptRoot "apache-maven-$MavenVersion"
Rename-Item -Path $ExtractedDir -NewName ".mvn-portable"

Remove-Item -Path $ZipFile -Force

Write-Host "Maven installed to $InstallDir"
Write-Host "You can verify it by running: .mvn-portable/bin/mvn -version"
