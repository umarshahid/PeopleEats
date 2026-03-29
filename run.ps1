$ErrorActionPreference = 'Stop'

$outDir = Join-Path $PSScriptRoot 'out'
if (!(Test-Path -LiteralPath $outDir)) {
    New-Item -ItemType Directory -Path $outDir | Out-Null
}

$files = Get-ChildItem -Recurse -Filter *.java -Path (Join-Path $PSScriptRoot 'src') |
    ForEach-Object { $_.FullName }

if (!$files -or $files.Count -eq 0) {
    throw "No Java files found under src."
}

Write-Host "Compiling $($files.Count) Java files..."
javac -encoding UTF-8 -d $outDir $files

Write-Host "Running Main..."
$libDir = Join-Path $PSScriptRoot 'lib'
$classPath = $outDir
if (Test-Path -LiteralPath $libDir) {
    $classPath = "$outDir;$libDir\*"
}
java -cp $classPath Main
