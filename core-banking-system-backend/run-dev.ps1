# PowerShell helper to load .env into the process environment and run the Spring Boot app
# Run this from the project root in a PowerShell prompt

if (Test-Path -Path .env) {
    Get-Content .env |
      Where-Object { $_ -and -not ($_.TrimStart()).StartsWith('#') } |
      ForEach-Object {
        $p = $_ -split '=',2
        if ($p.Length -eq 2) { Set-Item -Path env:$($p[0]) -Value $p[1] }
      }
}

if ($env:SPRING_DATASOURCE_URL) {
  $len = [Math]::Min(40, $env:SPRING_DATASOURCE_URL.Length)
  Write-Host "JDBC URL starts with: $($env:SPRING_DATASOURCE_URL.Substring(0,$len))..."
} else {
  Write-Host "SPRING_DATASOURCE_URL not set"
}

mvn spring-boot:run
