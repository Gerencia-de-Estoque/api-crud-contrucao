# Análise de Qualidade de Código

Este projeto possui ferramentas de análise estática de código configuradas que geram uma **nota de qualidade**.

## Como Executar

### Opção 1: Script Completo com Nota
Execute o script que faz toda a análise e mostra uma nota final:

```bash
./run-quality-check.sh
```

Este script irá:
1. Compilar o projeto
2. Executar testes
3. Executar Checkstyle, PMD e SpotBugs
4. Gerar relatório de cobertura (JaCoCo)
5. **Calcular e mostrar uma nota de 0 a 125 pontos**

### Opção 2: Comandos Maven Individuais

#### Executar todas as verificações:
```bash
mvn clean verify
```

#### Gerar relatórios HTML detalhados:
```bash
mvn site
```

#### Executar apenas Checkstyle:
```bash
mvn checkstyle:check
```

#### Executar apenas PMD:
```bash
mvn pmd:check
```

#### Executar apenas SpotBugs:
```bash
mvn spotbugs:check
```

#### Gerar relatório de cobertura:
```bash
mvn test jacoco:report
```

## Sistema de Pontuação

A nota final é calculada com base em 5 critérios (125 pontos no total):

### 1. Build (25 pontos)
- ✅ Build passou: 25 pontos
- ❌ Build falhou: 0 pontos

### 2. Checkstyle (25 pontos)
- 0 violações: 25 pontos
- 1-4 violações: 20 pontos
- 5-9 violações: 15 pontos
- 10-19 violações: 10 pontos
- 20+ violações: 5 pontos

### 3. PMD (25 pontos)
- 0 violações: 25 pontos
- 1-4 violações: 20 pontos
- 5-9 violações: 15 pontos
- 10-19 violações: 10 pontos
- 20+ violações: 5 pontos

### 4. SpotBugs (25 pontos)
- 0 bugs: 25 pontos
- 1-2 bugs: 20 pontos
- 3-4 bugs: 15 pontos
- 5-9 bugs: 10 pontos
- 10+ bugs: 5 pontos

### 5. Cobertura de Testes - JaCoCo (25 pontos)
- 100% cobertura: 25 pontos
- 80-99%: 20-24 pontos
- 60-79%: 15-19 pontos
- 40-59%: 10-14 pontos
- 20-39%: 5-9 pontos
- 0-19%: 0-4 pontos

## Conceitos

- **90-125 pontos: A** - Excelente qualidade
- **80-89 pontos: B** - Boa qualidade
- **70-79 pontos: C** - Qualidade aceitável
- **60-69 pontos: D** - Precisa melhorias
- **0-59 pontos: F** - Qualidade inadequada (falha na verificação)

## Relatórios Gerados

Após executar `./run-quality-check.sh` ou `mvn site`, você pode visualizar relatórios detalhados em:

- **Site completo**: `target/site/index.html` (abra no navegador)
- **Checkstyle**: `target/site/checkstyle.html`
- **PMD**: `target/site/pmd.html`
- **SpotBugs**: `target/site/spotbugs.html`
- **JaCoCo (Cobertura)**: `target/site/jacoco/index.html`

## Ferramentas Configuradas

### Checkstyle
Verifica estilo de código e convenções:
- Indentação (4 espaços)
- Comprimento de linha (máx 140 caracteres)
- Nomenclatura de variáveis e métodos
- Organização de imports
- Uso de chaves e espaços

### PMD
Detecta problemas de código:
- Código duplicado (CPD)
- Código morto
- Expressões complexas
- Práticas ruins de programação

### SpotBugs
Encontra bugs potenciais:
- Null pointer exceptions
- Problemas de concorrência
- Erros de lógica
- Vulnerabilidades de segurança

### JaCoCo
Mede cobertura de testes:
- Cobertura de linhas
- Cobertura de branches
- Cobertura de métodos
- Cobertura de classes

## Integração com CI/CD

Você pode integrar a verificação de qualidade no seu pipeline CI/CD:

```yaml
# Exemplo para GitHub Actions
- name: Quality Check
  run: ./run-quality-check.sh
```

O script retorna código de erro se a nota for menor que 60 pontos, falhando o pipeline automaticamente.
