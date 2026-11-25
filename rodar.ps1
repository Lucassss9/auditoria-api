# Sobe o jar lendo as variaveis do .env, o mesmo arquivo que o compose usa.
# Nenhuma senha mora aqui dentro: por isso este arquivo pode ir pro Git.
#
# Uso:  .\rodar.ps1

$arquivo = Join-Path $PSScriptRoot '.env'

if (-not (Test-Path $arquivo)) {
    Write-Error "Nao achei o .env em $PSScriptRoot. Ele guarda a senha do banco e as credenciais do CF."
    exit 1
}

$lidas = 0

foreach ($linha in Get-Content $arquivo) {
    $texto = $linha.Trim()

    if ($texto -eq '' -or $texto.StartsWith('#')) { continue }

    $corte = $texto.IndexOf('=')
    if ($corte -lt 1) { continue }

    $nome  = $texto.Substring(0, $corte).Trim()
    $valor = $texto.Substring($corte + 1).Trim()

    # Tira aspas em volta, se tiver. O valor vai cru pro processo.
    if ($valor.Length -ge 2) {
        $primeira = $valor[0]
        $ultima   = $valor[$valor.Length - 1]
        if (($primeira -eq '"' -and $ultima -eq '"') -or ($primeira -eq "'" -and $ultima -eq "'")) {
            $valor = $valor.Substring(1, $valor.Length - 2)
        }
    }

    [Environment]::SetEnvironmentVariable($nome, $valor, 'Process')
    $lidas++
}

Write-Host "$lidas variaveis lidas do .env"

$faltando = @(
    'POSTGRES_DB', 'POSTGRES_PASSWORD',
    'CF_URL', 'CF_EMAIL', 'CF_SENHA',
    'AUDITORIA_ADMIN_EMAIL', 'AUDITORIA_ADMIN_SENHA'
) | Where-Object { -not [Environment]::GetEnvironmentVariable($_) }

if ($faltando) {
    Write-Error "Faltam no .env: $($faltando -join ', ')"
    exit 1
}

$jar = Join-Path $PSScriptRoot 'target\auditoria-api-0.0.1-SNAPSHOT.jar'

if (-not (Test-Path $jar)) {
    Write-Error "Nao achei o jar. Rode primeiro:  .\mvnw -DskipTests package"
    exit 1
}

java -jar $jar
