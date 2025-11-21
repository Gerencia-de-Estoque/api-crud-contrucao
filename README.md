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

> O banco é compartilhado com `api-autentic`, logo essa stack deve subir antes da API de autenticação.

## Como executar

```bash
cd API-LOJA-DE-CONSTRU-O
docker compose up -d --build
```

URLs úteis:

- API REST: http://localhost:8090
- Adminer: http://localhost:8082 (servidor `db`, usuário `root`, senha `root`)

Para encerrar:

```bash
docker compose down
```

## Testes

Rode a suíte completa (Checkstyle/PMD/SpotBugs + testes) na pasta `springboot/demo`:

```bash
cd API-LOJA-DE-CONSTRU-O/springboot/demo
./mvnw -Dstyle.color=always \
       -Dsurefire.reportFormat=plain \
       -Dsurefire.printSummary=true \
       -Dsurefire.useFile=false \
       verify
```

## Endpoints principais

| Método | Caminho | Descrição |
| --- | --- | --- |
| `GET /api/FILIAL` | Lista filiais (com login/ativo) |
| `POST /api/FILIAL` | Cria/atualiza uma filial (usa os campos `nome`, `login`, `senha`, `ativo`) |
| `GET /api/FERRAMENTA` | Lista ferramentas cadastradas |
| `GET /api/MATERIAL-CONSTRUCAO` | Lista materiais de construção |
| `POST/PUT/DELETE /api/FERRAMENTA|MATERIAL-CONSTRUCAO` | CRUD completo via payloads `FerramentaDTO`/`MaterialConstrucaoDTO` |

Todas as rotas exigem o token emitido por `api-autentic`, exceto o `POST /api/FILIAL`, liberado para criação de novos usuários.
