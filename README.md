# API Loja de Construção

Serviço principal que expõe os recursos de filiais, ferramentas e materiais de construção. Ele também provisiona o MySQL utilizado por toda a stack.

## Pré‑requisitos

- Docker e Docker Compose v2

## Serviços do `docker-compose.yml`

| Serviço | Descrição | Porta |
| --- | --- | --- |
| `db` | MySQL 8 com banco `dbspringboot` exposto em `3307` | 3307 |
| `api` | Aplicação Spring Boot (porta interna 8089 → host 8090) | 8090 |
| `adminer` | Adminer conectado ao banco compartilhado | 8082 |

## Variáveis relevantes

| Variável | Descrição | Padrão |
| --- | --- | --- |
| `SPRING_DATASOURCE_*` | Credenciais e URL do banco | `root` / `jdbc:mysql://db:3306/dbspringboot` |
| `PORT` | Porta interna da API | `8089` |
| `SPRING_SQL_INIT_MODE` | Mantido como `always` para rodar `schema.sql` | `always` |

> O banco é compartilhado com `api-autenticacao` via porta 3307 do host, então suba esta stack antes da API de autenticação.

## Como executar

```bash
cd api-crud-contrucao
docker compose up -d --build
```

URLs úteis:

- API REST: http://localhost:8090
- Adminer: http://localhost:8082 (servidor `db`, usuário `root`, senha `root`)

Para encerrar:

```bash
docker compose down
```

## Lint (Checkstyle/PMD/SpotBugs) com Nota de Qualidade

### Verificação Rápida com Nota (Recomendado)

Execute o script que mostra uma **nota de 0-100** baseada na qualidade do código:

```bash
cd api-crud-contrucao/springboot/demo
./quick-lint-score.sh
```

Este script executa Checkstyle, PMD e SpotBugs e mostra:
- Número de violações de cada ferramenta
- Pontuação individual (0-25 pontos cada)
- **Nota final do lint (0-100)** com conceito (A, B, C, D ou F)

### Verificação Completa com Testes e Cobertura

Para obter a nota completa (0-125) incluindo cobertura de testes:

```bash
cd api-crud-contrucao/springboot/demo
./run-quality-check.sh
```

Este script gera relatórios HTML detalhados em `target/site/` que você pode abrir no navegador.

### Comandos Maven Diretos

Se preferir usar Maven diretamente (sem nota):

```bash
cd api-crud-contrucao/springboot/demo
./mvnw -DskipTests -Dstyle.color=always \
       checkstyle:check pmd:check spotbugs:check
```

📖 Para mais detalhes sobre o sistema de pontuação, veja [springboot/demo/QUALITY_CHECK.md](springboot/demo/QUALITY_CHECK.md)

## Testes

Rode a suíte completa (Checkstyle/PMD/SpotBugs + testes) na pasta `springboot/demo`:

```bash
cd api-crud-contrucao/springboot/demo
./mvnw -Dstyle.color=always \
       -Dsurefire.reportFormat=plain \
       -Dsurefire.printSummary=true \
       -Dsurefire.useFile=false \
       verify
```

Tipos de testes incluídos (em `springboot/demo/src/test/java`):
- Unitários de serviço (Mockito) em `api/service/*ServiceTest.java` para Filial, Ferramenta e Material de Construção.
- Integração (MockMvc) em `api/FilialIntegrationTest.java`, subindo o contexto Spring Boot com perfil `test` e banco H2 em memória para validar o endpoint `POST /api/FILIAL` e persistência com senha hash.

## Endpoints principais

| Método | Caminho | Descrição |
| --- | --- | --- |
| `GET /api/FILIAL` | Lista filiais (com login/ativo) |
| `POST /api/FILIAL` | Cria/atualiza uma filial (usa os campos `nome`, `login`, `senha`, `ativo`) |
| `GET /api/FERRAMENTA` | Lista ferramentas cadastradas |
| `GET /api/MATERIAL-CONSTRUCAO` | Lista materiais de construção |
| `POST/PUT/DELETE /api/FERRAMENTA|MATERIAL-CONSTRUCAO` | CRUD completo via payloads `FerramentaDTO`/`MaterialConstrucaoDTO` |

Todas as rotas exigem o token emitido por `api-autenticacao`, exceto o `POST /api/FILIAL`, liberado para criação de novos usuários.
