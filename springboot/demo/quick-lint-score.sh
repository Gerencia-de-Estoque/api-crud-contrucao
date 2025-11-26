#!/bin/bash

# Script rápido para ver a nota do lint (sem executar testes)

echo "=========================================="
echo "  Análise Rápida de Lint"
echo "=========================================="
echo ""

# Cores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}Executando análise de código (sem testes)...${NC}"
./mvnw clean compile checkstyle:checkstyle pmd:pmd spotbugs:spotbugs -DskipTests=true 2>&1 | tee lint.log

if [ ${PIPESTATUS[0]} -ne 0 ]; then
    echo -e "${RED}Compilação falhou!${NC}"
    BUILD_STATUS="FALHOU"
    BUILD_POINTS=0
else
    echo -e "${GREEN}Compilação OK!${NC}"
    BUILD_STATUS="PASSOU"
    BUILD_POINTS=25
fi

echo ""
echo "=========================================="
echo "  Resultados"
echo "=========================================="
echo ""

# Checkstyle
if [ -f "target/checkstyle-result.xml" ]; then
    CHECKSTYLE_VIOLATIONS=$(grep -c '<error' target/checkstyle-result.xml 2>/dev/null || true)
    if [ -z "$CHECKSTYLE_VIOLATIONS" ]; then
        CHECKSTYLE_VIOLATIONS=0
    fi
else
    CHECKSTYLE_VIOLATIONS=0
fi

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

# PMD
if [ -f "target/pmd.xml" ]; then
    PMD_VIOLATIONS=$(grep -c '<violation' target/pmd.xml 2>/dev/null || true)
    if [ -z "$PMD_VIOLATIONS" ]; then
        PMD_VIOLATIONS=0
    fi
else
    PMD_VIOLATIONS=0
fi

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

# SpotBugs
if [ -f "target/spotbugsXml.xml" ]; then
    SPOTBUGS_BUGS=$(grep -c '<BugInstance' target/spotbugsXml.xml 2>/dev/null || true)
    if [ -z "$SPOTBUGS_BUGS" ]; then
        SPOTBUGS_BUGS=0
    fi
else
    SPOTBUGS_BUGS=0
fi

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
echo "  Bugs: $SPOTBUGS_BUGS"
echo "  Pontuação: $SPOTBUGS_POINTS/25"
echo ""

# Nota parcial (sem cobertura de testes)
LINT_TOTAL=$((BUILD_POINTS + CHECKSTYLE_POINTS + PMD_POINTS + SPOTBUGS_POINTS))
MAX_LINT=100

echo "=========================================="
echo -e "${BLUE}  NOTA DO LINT${NC}"
echo "=========================================="
echo ""
echo "  Build:           $BUILD_POINTS/25"
echo "  Checkstyle:      $CHECKSTYLE_POINTS/25"
echo "  PMD:             $PMD_POINTS/25"
echo "  SpotBugs:        $SPOTBUGS_POINTS/25"
echo "  --------------------------------"

if [ "$LINT_TOTAL" -ge 90 ]; then
    COLOR=$GREEN
    GRADE="A"
elif [ "$LINT_TOTAL" -ge 80 ]; then
    COLOR=$GREEN
    GRADE="B"
elif [ "$LINT_TOTAL" -ge 70 ]; then
    COLOR=$YELLOW
    GRADE="C"
elif [ "$LINT_TOTAL" -ge 60 ]; then
    COLOR=$YELLOW
    GRADE="D"
else
    COLOR=$RED
    GRADE="F"
fi

echo -e "  ${COLOR}TOTAL LINT:      $LINT_TOTAL/$MAX_LINT (${GRADE})${NC}"
echo ""
echo -e "${YELLOW}  Nota: Execute './run-quality-check.sh' para incluir"
echo -e "  cobertura de testes e obter nota completa (0-125).${NC}"
echo ""
echo "=========================================="
echo ""

# Detalhamento de problemas
if [ "$CHECKSTYLE_VIOLATIONS" -gt 0 ] || [ "$PMD_VIOLATIONS" -gt 0 ] || [ "$SPOTBUGS_BUGS" -gt 0 ]; then
    echo "Para ver detalhes dos problemas:"

    if [ "$CHECKSTYLE_VIOLATIONS" -gt 0 ]; then
        echo "  - Checkstyle: cat target/checkstyle-result.xml"
    fi

    if [ "$PMD_VIOLATIONS" -gt 0 ]; then
        echo "  - PMD: cat target/pmd.xml"
    fi

    if [ "$SPOTBUGS_BUGS" -gt 0 ]; then
        echo "  - SpotBugs: cat target/spotbugsXml.xml"
    fi

    echo ""
    echo "Ou gere o site completo com: mvn site"
    echo "E abra: target/site/index.html"
    echo ""
fi
