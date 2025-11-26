#!/bin/bash

# Script para executar análise de qualidade de código e gerar nota

echo "=========================================="
echo "  Análise de Qualidade de Código"
echo "=========================================="
echo ""

# Cores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Executar verificação completa
echo -e "${BLUE}Executando verificações...${NC}"
./mvnw clean verify site -DskipTests=false 2>&1 | tee build.log

# Verificar se a build passou
if [ ${PIPESTATUS[0]} -ne 0 ]; then
    echo -e "${RED}Build falhou! Verifique os erros acima.${NC}"
    BUILD_STATUS="FALHOU"
    BUILD_POINTS=0
else
    echo -e "${GREEN}Build passou com sucesso!${NC}"
    BUILD_STATUS="PASSOU"
    BUILD_POINTS=25
fi

echo ""
echo "=========================================="
echo "  Resultados da Análise"
echo "=========================================="
echo ""

# Contar violações do Checkstyle
CHECKSTYLE_VIOLATIONS=$(grep -c "Checkstyle violations" build.log 2>/dev/null || true)
if [ -z "$CHECKSTYLE_VIOLATIONS" ]; then
    CHECKSTYLE_VIOLATIONS=0
fi
CHECKSTYLE_FILES=$(grep "Checkstyle" build.log | grep -oP '\d+(?= files)' | head -1 || true)
if [ -z "$CHECKSTYLE_FILES" ]; then
    CHECKSTYLE_FILES=0
fi

# Calcular pontos do Checkstyle (25 pontos máximo)
if [ "$CHECKSTYLE_VIOLATIONS" -eq 0 ]; then
    CHECKSTYLE_POINTS=25
elif [ "$CHECKSTYLE_VIOLATIONS" -lt 5 ]; then
    CHECKSTYLE_POINTS=20
elif [ "$CHECKSTYLE_VIOLATIONS" -lt 10 ]; then
    CHECKSTYLE_POINTS=15
elif [ "$CHECKSTYLE_VIOLATIONS" -lt 20 ]; then
    CHECKSTYLE_POINTS=10
else
    CHECKSTYLE_POINTS=5
fi

echo -e "${BLUE}Checkstyle:${NC}"
echo "  Violações: $CHECKSTYLE_VIOLATIONS"
echo "  Pontuação: $CHECKSTYLE_POINTS/25"
echo ""

# Contar violações do PMD
PMD_VIOLATIONS=$(grep -oP 'PMD Failure: \K\d+' build.log 2>/dev/null || true)
if [ -z "$PMD_VIOLATIONS" ]; then
    PMD_VIOLATIONS=0
fi

# Calcular pontos do PMD (25 pontos máximo)
if [ "$PMD_VIOLATIONS" -eq 0 ]; then
    PMD_POINTS=25
elif [ "$PMD_VIOLATIONS" -lt 5 ]; then
    PMD_POINTS=20
elif [ "$PMD_VIOLATIONS" -lt 10 ]; then
    PMD_POINTS=15
elif [ "$PMD_VIOLATIONS" -lt 20 ]; then
    PMD_POINTS=10
else
    PMD_POINTS=5
fi

echo -e "${BLUE}PMD:${NC}"
echo "  Violações: $PMD_VIOLATIONS"
echo "  Pontuação: $PMD_POINTS/25"
echo ""

# Contar bugs do SpotBugs
SPOTBUGS_BUGS=$(grep -oP 'SpotBugs has found \K\d+' build.log 2>/dev/null || true)
if [ -z "$SPOTBUGS_BUGS" ]; then
    SPOTBUGS_BUGS=0
fi

# Calcular pontos do SpotBugs (25 pontos máximo)
if [ "$SPOTBUGS_BUGS" -eq 0 ]; then
    SPOTBUGS_POINTS=25
elif [ "$SPOTBUGS_BUGS" -lt 3 ]; then
    SPOTBUGS_POINTS=20
elif [ "$SPOTBUGS_BUGS" -lt 5 ]; then
    SPOTBUGS_POINTS=15
elif [ "$SPOTBUGS_BUGS" -lt 10 ]; then
    SPOTBUGS_POINTS=10
else
    SPOTBUGS_POINTS=5
fi

echo -e "${BLUE}SpotBugs:${NC}"
echo "  Bugs encontrados: $SPOTBUGS_BUGS"
echo "  Pontuação: $SPOTBUGS_POINTS/25"
echo ""

# Cobertura de testes (JaCoCo)
if [ -f "target/site/jacoco/index.html" ]; then
    COVERAGE=$(grep -oP 'Total.*?(\d+)%' target/site/jacoco/index.html | grep -oP '\d+' | head -1 || true)
    if [ -z "$COVERAGE" ]; then
        COVERAGE=0
    fi
    COVERAGE_POINTS=$((COVERAGE / 4))
    if [ "$COVERAGE_POINTS" -gt 25 ]; then
        COVERAGE_POINTS=25
    fi

    echo -e "${BLUE}Cobertura de Testes:${NC}"
    echo "  Cobertura: ${COVERAGE}%"
    echo "  Pontuação: $COVERAGE_POINTS/25"
    echo ""
else
    COVERAGE=0
    COVERAGE_POINTS=0
    echo -e "${YELLOW}Cobertura de testes não disponível${NC}"
    echo ""
fi

# Calcular nota final
TOTAL_POINTS=$((BUILD_POINTS + CHECKSTYLE_POINTS + PMD_POINTS + SPOTBUGS_POINTS + COVERAGE_POINTS))

echo "=========================================="
echo -e "${BLUE}  NOTA FINAL DA QUALIDADE${NC}"
echo "=========================================="
echo ""
echo "  Build:           $BUILD_POINTS/25"
echo "  Checkstyle:      $CHECKSTYLE_POINTS/25"
echo "  PMD:             $PMD_POINTS/25"
echo "  SpotBugs:        $SPOTBUGS_POINTS/25"
echo "  Cobertura:       $COVERAGE_POINTS/25"
echo "  --------------------------------"

# Determinar cor baseada na nota
if [ "$TOTAL_POINTS" -ge 90 ]; then
    COLOR=$GREEN
    GRADE="A"
elif [ "$TOTAL_POINTS" -ge 80 ]; then
    COLOR=$GREEN
    GRADE="B"
elif [ "$TOTAL_POINTS" -ge 70 ]; then
    COLOR=$YELLOW
    GRADE="C"
elif [ "$TOTAL_POINTS" -ge 60 ]; then
    COLOR=$YELLOW
    GRADE="D"
else
    COLOR=$RED
    GRADE="F"
fi

echo -e "  ${COLOR}TOTAL:           $TOTAL_POINTS/125 (${GRADE})${NC}"
echo ""
echo "=========================================="
echo ""

# Mostrar onde encontrar relatórios detalhados
echo "Relatórios detalhados disponíveis em:"
echo "  - Checkstyle:  target/checkstyle-result.xml"
echo "  - PMD:         target/pmd.xml"
echo "  - SpotBugs:    target/spotbugsXml.xml"
echo "  - JaCoCo:      target/site/jacoco/index.html"
echo "  - Site completo: target/site/index.html"
echo ""

# Retornar código de erro se nota for muito baixa
if [ "$TOTAL_POINTS" -lt 60 ]; then
    echo -e "${RED}Qualidade do código abaixo do aceitável!${NC}"
    exit 1
fi
