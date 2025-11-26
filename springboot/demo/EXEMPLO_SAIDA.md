# Exemplo de Saída dos Scripts de Qualidade

## Script Rápido (`./quick-lint-score.sh`)

Este script executa apenas a análise de código estático (sem testes) e mostra uma nota de 0-100:

```
==========================================
  Análise Rápida de Lint
==========================================

Executando análise de código (sem testes)...
[INFO] Scanning for projects...
[INFO] Building demo 0.0.1-SNAPSHOT
[INFO] Compiling 15 source files to target/classes
[INFO] Running Checkstyle...
[INFO] Running PMD...
[INFO] Running SpotBugs...

Compilação OK!

==========================================
  Resultados
==========================================

Checkstyle:
  Violações: 3
  Pontuação: 20/25

PMD:
  Violações: 1
  Pontuação: 20/25

SpotBugs:
  Bugs: 0
  Pontuação: 25/25

==========================================
  NOTA DO LINT
==========================================

  Build:           25/25
  Checkstyle:      20/25
  PMD:             20/25
  SpotBugs:        25/25
  --------------------------------
  TOTAL LINT:      90/100 (A)

  Nota: Execute './run-quality-check.sh' para incluir
  cobertura de testes e obter nota completa (0-125).

==========================================

Para ver detalhes dos problemas:
  - Checkstyle: cat target/checkstyle-result.xml
  - PMD: cat target/pmd.xml

Ou gere o site completo com: mvn site
E abra: target/site/index.html
```

## Script Completo (`./run-quality-check.sh`)

Este script executa toda a verificação incluindo testes e cobertura, gerando uma nota de 0-125:

```
==========================================
  Análise de Qualidade de Código
==========================================

Executando verificações...
[INFO] Scanning for projects...
[INFO] Building demo 0.0.1-SNAPSHOT
[INFO] Running tests...
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running Checkstyle...
[INFO] Running PMD...
[INFO] Running SpotBugs...
[INFO] Running JaCoCo...
[INFO] Generating site...

Build passou com sucesso!

==========================================
  Resultados da Análise
==========================================

Checkstyle:
  Violações: 3
  Pontuação: 20/25

PMD:
  Violações: 1
  Pontuação: 20/25

SpotBugs:
  Bugs encontrados: 0
  Pontuação: 25/25

Cobertura de Testes:
  Cobertura: 85%
  Pontuação: 21/25

==========================================
  NOTA FINAL DA QUALIDADE
==========================================

  Build:           25/25
  Checkstyle:      20/25
  PMD:             20/25
  SpotBugs:        25/25
  Cobertura:       21/25
  --------------------------------
  TOTAL:           111/125 (A)

==========================================

Relatórios detalhados disponíveis em:
  - Checkstyle:  target/checkstyle-result.xml
  - PMD:         target/pmd.xml
  - SpotBugs:    target/spotbugsXml.xml
  - JaCoCo:      target/site/jacoco/index.html
  - Site completo: target/site/index.html
```

## Interpretação das Notas

### Conceitos
- **A (90-125 pontos)**: Excelente qualidade - código pronto para produção
- **B (80-89 pontos)**: Boa qualidade - pequenos ajustes recomendados
- **C (70-79 pontos)**: Qualidade aceitável - melhorias necessárias
- **D (60-69 pontos)**: Precisa de melhorias - revisão recomendada
- **F (0-59 pontos)**: Qualidade inadequada - refatoração necessária

### Pontuação por Categoria

#### Build (25 pontos)
- ✅ 25 pontos: Compilação bem-sucedida
- ❌ 0 pontos: Falha na compilação

#### Checkstyle (25 pontos)
Verifica estilo e convenções:
- 25 pontos: Nenhuma violação
- 20 pontos: 1-4 violações (ex: problemas de formatação)
- 15 pontos: 5-9 violações
- 10 pontos: 10-19 violações
- 5 pontos: 20+ violações

#### PMD (25 pontos)
Detecta código problemático:
- 25 pontos: Nenhuma violação
- 20 pontos: 1-4 violações (ex: código duplicado)
- 15 pontos: 5-9 violações
- 10 pontos: 10-19 violações
- 5 pontos: 20+ violações

#### SpotBugs (25 pontos)
Encontra bugs potenciais:
- 25 pontos: Nenhum bug
- 20 pontos: 1-2 bugs (ex: null pointer potencial)
- 15 pontos: 3-4 bugs
- 10 pontos: 5-9 bugs
- 5 pontos: 10+ bugs

#### Cobertura de Testes (25 pontos)
Apenas no script completo:
- 25 pontos: 100% de cobertura
- 21 pontos: 85% de cobertura
- 15 pontos: 60% de cobertura
- 10 pontos: 40% de cobertura
- 5 pontos: 20% de cobertura

## Dicas para Melhorar a Nota

1. **Checkstyle**: Execute `mvn checkstyle:checkstyle` e corrija formatação
2. **PMD**: Verifique código duplicado e complexidade ciclomática
3. **SpotBugs**: Revise warnings sobre null pointers e resource leaks
4. **Cobertura**: Adicione testes unitários para classes sem cobertura
