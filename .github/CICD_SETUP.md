# Guia de Configuração CI/CD

Este documento fornece instruções detalhadas para configurar e usar os pipelines CI/CD da API CRUD Construção.

## Arquitetura do Pipeline

```
┌─────────────────────────────────────────────────────────────┐
│                         GitHub Push/PR                       │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                    CI - Continuous Integration               │
├─────────────────────────────────────────────────────────────┤
│  1. Build & Test                                            │
│     ├─ Compile Java 17                                      │
│     ├─ Run Unit Tests                                       │
│     ├─ Run Integration Tests                                │
│     └─ Generate JaCoCo Coverage                             │
│                                                              │
│  2. Code Quality                                            │
│     ├─ Checkstyle (Style)                                   │
│     ├─ PMD (Code Analysis)                                  │
│     ├─ SpotBugs (Bug Detection)                             │
│     └─ SonarCloud (Quality Gate)                            │
│                                                              │
│  3. Docker Build                                            │
│     └─ Build & Push to Docker Hub                           │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼ (if main branch or tag)
┌─────────────────────────────────────────────────────────────┐
│                   CD - Continuous Deployment                 │
├─────────────────────────────────────────────────────────────┤
│  1. Deploy to Railway                                       │
│     ├─ Trigger Railway Deployment                           │
│     ├─ Wait for Service Start                               │
│     └─ Health Check Validation                              │
│                                                              │
│  2. Docker Release                                          │
│     └─ Tag & Push Production Image                          │
│                                                              │
│  3. GitHub Release (tags only)                              │
│     ├─ Generate Changelog                                   │
│     ├─ Attach JAR Artifacts                                 │
│     └─ Publish Release Notes                                │
└─────────────────────────────────────────────────────────────┘
```

## Configuração Inicial

### 1. Configurar Secrets no GitHub

Acesse: `Settings` → `Secrets and variables` → `Actions`

#### Secrets Obrigatórios

**Para Docker Hub:**
```bash
DOCKER_USERNAME=seu-usuario-dockerhub
DOCKER_PASSWORD=sua-senha-ou-token-dockerhub
```

**Para Railway Deploy:**
```bash
RAILWAY_TOKEN=seu-token-railway
```

Como obter o Railway Token:
```bash
# Instale o Railway CLI
npm i -g @railway/cli

# Faça login
railway login

# Obtenha o token
railway whoami --token
```

#### Secrets Opcionais (Recomendados)

**Para SonarCloud:**
```bash
SONAR_TOKEN=seu-token-sonarcloud
```

Como configurar:
1. Acesse [SonarCloud](https://sonarcloud.io)
2. Importe o repositório
3. Gere um token em: My Account → Security → Generate Token

**Para Codecov:**
```bash
CODECOV_TOKEN=seu-token-codecov
```

Como configurar:
1. Acesse [Codecov](https://codecov.io)
2. Conecte o repositório
3. Copie o token fornecido

### 2. Configurar Environment no GitHub (Opcional)

Para proteção adicional, crie um environment "production":

1. Vá em `Settings` → `Environments` → `New environment`
2. Nome: `production`
3. Configure regras de proteção:
   - ✅ Required reviewers (1 revisor)
   - ✅ Wait timer (5 minutos)
   - ✅ Deployment branches: `main` only

### 3. Habilitar GitHub Actions

1. Vá em `Settings` → `Actions` → `General`
2. Em "Actions permissions", selecione:
   - ✅ Allow all actions and reusable workflows
3. Em "Workflow permissions", selecione:
   - ✅ Read and write permissions
   - ✅ Allow GitHub Actions to create and approve pull requests

### 4. Configurar Branch Protection (Recomendado)

Para `main` branch:
1. `Settings` → `Branches` → `Add rule`
2. Branch name pattern: `main`
3. Configure:
   - ✅ Require a pull request before merging
   - ✅ Require status checks to pass before merging
     - Status checks: `Build and Test`, `Code Quality Analysis`
   - ✅ Require branches to be up to date before merging

## Uso dos Workflows

### CI Workflow

**Trigger automático:**
- Push para `main` ou `develop`
- Pull Request para `main` ou `develop`

**Executar manualmente:**
1. Vá em `Actions` → `CI - Continuous Integration`
2. Clique em `Run workflow`
3. Selecione a branch

**O que verifica:**
- ✅ Compilação sem erros
- ✅ Testes passam (unitários + integração)
- ✅ Cobertura de código adequada
- ✅ Qualidade de código (Checkstyle, PMD, SpotBugs)
- ✅ Build Docker bem-sucedido

### CD Workflow

**Trigger automático:**
- Push para `main` (deploy automático)
- Push de tags `v*` (release)

**Executar manualmente:**
1. Vá em `Actions` → `CD - Continuous Deployment`
2. Clique em `Run workflow`
3. Selecione `main` branch

**Deploy para Railway:**
O workflow faz:
1. Deploy da aplicação
2. Aguarda 30 segundos
3. Verifica health check em `/actuator/health`
4. Notifica resultado

### Security Workflow

**Trigger automático:**
- Toda segunda-feira (cron)
- Push/PR para `main`

**O que verifica:**
- 🔒 Vulnerabilidades em dependências (OWASP)
- 🔒 Vulnerabilidades em containers (Trivy)
- 🔒 Análise de segurança de código (CodeQL)

**Executar manualmente:**
1. Vá em `Actions` → `Security Scan`
2. Clique em `Run workflow`

## Criando Releases

### Usando Tags

Para criar uma release automática:

```bash
# Atualize a versão no pom.xml se necessário
vim springboot/demo/pom.xml

# Commit e push
git add .
git commit -m "chore: bump version to 1.0.0"
git push origin main

# Crie e push a tag
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

Isso irá:
1. Disparar o CD workflow
2. Build e deploy para Railway
3. Publicar imagem Docker com tag `v1.0.0` e `latest`
4. Criar GitHub Release com:
   - Changelog automático
   - Arquivo JAR anexado
   - Instruções de uso do Docker

### Formato de Versionamento

Use [Semantic Versioning](https://semver.org/):
- `v1.0.0` - Major release
- `v1.1.0` - Minor release (features)
- `v1.0.1` - Patch release (bug fixes)

## Monitoramento e Logs

### Visualizar Logs dos Workflows

1. Vá em `Actions`
2. Clique no workflow desejado
3. Selecione a execução
4. Clique em cada job para ver logs detalhados

### Artefatos Gerados

O CI workflow gera artefatos que ficam disponíveis por 7 dias:
- `api-crud-jar` - JAR da aplicação
- `dependency-check-report` - Relatório de segurança

Para baixar:
1. Vá na execução do workflow
2. Role até "Artifacts"
3. Clique para download

### Status Badges

Os badges no README.md mostram status em tempo real:
- ![CI Badge](https://github.com/Gerencia-de-Estoque/api-crud-contrucao/actions/workflows/ci.yml/badge.svg) - Build e testes
- ![CD Badge](https://github.com/Gerencia-de-Estoque/api-crud-contrucao/actions/workflows/cd.yml/badge.svg) - Deploy
- ![Security Badge](https://github.com/Gerencia-de-Estoque/api-crud-contrucao/actions/workflows/security.yml/badge.svg) - Segurança

## Troubleshooting

### Build Falhando

**Problema:** Testes não passam
```bash
# Execute localmente para debug
cd springboot/demo
./mvnw clean test
```

**Problema:** Checkstyle/PMD falha
```bash
# Verifique violações localmente
cd springboot/demo
./mvnw checkstyle:check pmd:check spotbugs:check
```

### Deploy Falhando

**Problema:** Railway deployment timeout
- Verifique se o RAILWAY_TOKEN está correto
- Confirme que o serviço existe no Railway
- Aumente o timeout no workflow se necessário

**Problema:** Health check falha
- Verifique se Spring Actuator está habilitado
- Confirme a URL do health check
- Verifique logs no Railway

### Docker Build Falhando

**Problema:** Authentication error
- Verifique DOCKER_USERNAME e DOCKER_PASSWORD
- Teste login manualmente:
```bash
docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
```

**Problema:** Out of disk space
- GitHub Actions tem limite de espaço
- Otimize camadas do Dockerfile
- Use `.dockerignore` adequadamente

## Boas Práticas

### Para Desenvolvimento

1. **Sempre crie Pull Requests:**
   - Não faça push direto para `main`
   - Aguarde CI passar antes de merge

2. **Escreva mensagens de commit descritivas:**
   ```
   feat: adiciona endpoint de relatórios
   fix: corrige cálculo de estoque
   chore: atualiza dependências
   docs: melhora documentação da API
   ```

3. **Execute testes localmente:**
   ```bash
   ./mvnw clean verify
   ```

### Para Releases

1. **Atualize CHANGELOG.md** antes de criar tag
2. **Teste em staging** antes de fazer release
3. **Use tags anotadas:**
   ```bash
   git tag -a v1.0.0 -m "Descrição da release"
   ```

### Para Segurança

1. **Revise relatórios semanais** do Security workflow
2. **Mantenha dependências atualizadas**
3. **Não commite secrets** no código
4. **Use `.env` files** para configurações locais

## Recursos Adicionais

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Railway Deployment Guide](https://docs.railway.app/deploy/deployments)
- [Docker Hub Documentation](https://docs.docker.com/docker-hub/)
- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [Codecov Documentation](https://docs.codecov.com/)

## Suporte

Para problemas ou dúvidas:
1. Verifique os logs do workflow
2. Consulte este guia
3. Abra uma issue no repositório
