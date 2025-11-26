# ✅ CI Simples - Testes Automáticos

## 🎯 O que faz

Este projeto tem **CI (Integração Contínua) configurado** para rodar testes automaticamente a cada commit.

**Sem complicação. Sem configuração. Zero secrets necessários.**

---

## 🚀 Como usar

### 1. Faça seu commit normalmente

```bash
# Faça suas alterações
git add .
git commit -m "adiciona nova funcionalidade"
git push origin main
```

### 2. Pronto!

O GitHub Actions vai **automaticamente**:
- ✅ Compilar o código
- ✅ Rodar todos os testes
- ✅ Gerar relatório de cobertura
- ✅ Verificar qualidade do código

### 3. Veja o resultado

- **No README**: Badge mostra se passou ✅ ou falhou ❌
- **No GitHub**: https://github.com/Gerencia-de-Estoque/api-crud-contrucao/actions
- **Em PRs**: Status check aparece automaticamente

---

## 📊 O que é testado

```
Push → GitHub Actions
  │
  ├─> Compilar com Maven ✅
  ├─> Testes Unitários ✅
  ├─> Testes de Integração ✅
  ├─> Cobertura JaCoCo ✅
  └─> Qualidade (Checkstyle, PMD, SpotBugs) ⚠️
```

**Nota:** A verificação de qualidade mostra avisos mas não bloqueia o build.

---

## 🔍 Ver logs detalhados

1. Acesse: https://github.com/Gerencia-de-Estoque/api-crud-contrucao/actions
2. Clique no workflow que você quer ver
3. Clique em "Build e Testes"
4. Veja logs de cada etapa

---

## 🧪 Testar localmente antes do push

```bash
cd springboot/demo

# Rodar testes
./mvnw clean test

# Rodar testes + qualidade
./mvnw clean verify
```

---

## 🛡️ Branch Protection (Opcional)

Para **forçar** que os testes passem antes de fazer merge:

1. Vá em: **Settings → Branches → Add rule**
2. Branch name pattern: `main`
3. Marque: **Require status checks to pass before merging**
4. Selecione: **Build e Testes**
5. Salve

Agora ninguém pode fazer merge se os testes estiverem falhando! 🎉

---

## ❓ FAQ

### O workflow não rodou?

Verifique se o arquivo existe:
```bash
ls .github/workflows/ci.yml
```

### Os testes falharam?

Execute localmente para debug:
```bash
cd springboot/demo
./mvnw clean test -X  # -X para modo verbose
```

### Quanto custa?

**Grátis!** GitHub Actions oferece 2000 minutos/mês para repositórios públicos.
Este workflow usa ~5 minutos por execução.

### Posso desabilitar?

Sim, delete o arquivo:
```bash
rm .github/workflows/ci.yml
```

Ou desabilite em: Settings → Actions → Disable Actions

---

## 📝 Arquivo de Configuração

O workflow está em: [.github/workflows/ci.yml](.github/workflows/ci.yml)

É um arquivo simples que:
- Escuta push/PR para `main` e `develop`
- Configura Java 17
- Roda Maven com cache
- Executa testes
- Mostra resultado

**Você não precisa mexer nele!** Funciona automaticamente.

---

## 🎓 Próximos Passos

Depois que tudo estiver funcionando:

1. **Configure branch protection** (recomendado)
2. **Use Pull Requests** para trabalhar
3. **Veja o badge no README** mostrando status
4. **Adicione mais testes** conforme necessário

---

## ✅ Checklist

```
[ ] Arquivo .github/workflows/ci.yml existe
[ ] Fiz push para o GitHub
[ ] Vi o workflow executar em Actions
[ ] Badge aparece no README
[ ] Testes passaram ✅
```

**Tudo marcado? Parabéns! Seu CI está funcionando! 🎉**

---

## 🔗 Links Úteis

- **Ver workflows**: https://github.com/Gerencia-de-Estoque/api-crud-contrucao/actions
- **Documentação GitHub Actions**: https://docs.github.com/en/actions
- **Maven Surefire (testes)**: https://maven.apache.org/surefire/maven-surefire-plugin/

---

**Simples assim! Commit → Push → Testes automáticos. Sem complicação.** 🚀
