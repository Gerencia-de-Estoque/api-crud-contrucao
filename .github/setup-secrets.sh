#!/bin/bash

# Script para auxiliar na configuração de secrets do GitHub
# Não execute este script diretamente - use os comandos como referência

set -e

REPO_OWNER="Gerencia-de-Estoque"
REPO_NAME="api-crud-contrucao"

echo "=========================================="
echo "GitHub Secrets Setup Helper"
echo "=========================================="
echo ""
echo "Este script irá guiá-lo na configuração dos secrets necessários."
echo "IMPORTANTE: Você precisará ter o GitHub CLI instalado e configurado."
echo ""

# Verifica se gh CLI está instalado
if ! command -v gh &> /dev/null; then
    echo "❌ GitHub CLI não encontrado!"
    echo "📦 Instale em: https://cli.github.com/"
    exit 1
fi

echo "✅ GitHub CLI encontrado"
echo ""

# Verifica autenticação
if ! gh auth status &> /dev/null; then
    echo "❌ Não autenticado no GitHub CLI"
    echo "🔐 Execute: gh auth login"
    exit 1
fi

echo "✅ Autenticado no GitHub CLI"
echo ""

# Função para adicionar secret
add_secret() {
    local secret_name=$1
    local secret_description=$2
    local is_optional=$3

    echo "----------------------------------------"
    echo "Secret: $secret_name"
    echo "Descrição: $secret_description"

    if [ "$is_optional" = "true" ]; then
        echo "⚠️  OPCIONAL - Pode pular se não for usar"
    else
        echo "✅ OBRIGATÓRIO"
    fi

    echo ""
    read -p "Deseja configurar este secret agora? (s/n): " -n 1 -r
    echo ""

    if [[ $REPLY =~ ^[Ss]$ ]]; then
        read -sp "Digite o valor para $secret_name: " secret_value
        echo ""

        if [ -n "$secret_value" ]; then
            echo "$secret_value" | gh secret set "$secret_name" --repo="$REPO_OWNER/$REPO_NAME"
            echo "✅ Secret $secret_name configurado com sucesso!"
        else
            echo "⚠️  Valor vazio - secret não configurado"
        fi
    else
        echo "⏭️  Pulando $secret_name"
    fi
    echo ""
}

echo "=========================================="
echo "Configuração de Secrets"
echo "=========================================="
echo ""

# Secrets Docker Hub
echo "🐳 DOCKER HUB"
add_secret "DOCKER_USERNAME" "Usuário do Docker Hub" "false"
add_secret "DOCKER_PASSWORD" "Senha ou token do Docker Hub (recomendado usar access token)" "false"

# Secrets Railway
echo "🚂 RAILWAY"
echo "Para obter o token Railway:"
echo "  1. npm i -g @railway/cli"
echo "  2. railway login"
echo "  3. railway whoami --token"
echo ""
add_secret "RAILWAY_TOKEN" "Token de deploy do Railway" "false"

# Secrets opcionais
echo "📊 CODE QUALITY (Opcional)"
echo "SonarCloud: https://sonarcloud.io"
add_secret "SONAR_TOKEN" "Token do SonarCloud para análise de qualidade" "true"

echo "📈 CODECOV (Opcional)"
echo "Codecov: https://codecov.io"
add_secret "CODECOV_TOKEN" "Token do Codecov para relatórios de cobertura" "true"

echo ""
echo "=========================================="
echo "✅ Configuração concluída!"
echo "=========================================="
echo ""
echo "Próximos passos:"
echo "1. Verifique os secrets em: https://github.com/$REPO_OWNER/$REPO_NAME/settings/secrets/actions"
echo "2. Faça um push para disparar os workflows"
echo "3. Monitore em: https://github.com/$REPO_OWNER/$REPO_NAME/actions"
echo ""
echo "📚 Consulte .github/CICD_SETUP.md para mais informações"
echo ""
