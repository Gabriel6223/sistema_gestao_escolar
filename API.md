# 📖 API dos DAOs — Referência Rápida

> **Para o time de Front-End.** Lista completa dos métodos disponíveis para vocês usarem nas telas.
> **NÃO alterem os DAOs!** Se precisarem de algo novo, peçam ao Gabriel.

---

## 🔧 `com.sge.util.Conexao`

| Método | Retorno | Descrição |
|--------|---------|-----------|
| `getConnection()` | `Connection` | Abre conexão JDBC com MySQL |
| `testar()` | `boolean` | Testa se a conexão está funcionando |

> ⚠️ Configurar URL/usuário/senha em `Conexao.java` antes de usar.

---

## 👤 `com.sge.util.Sessao`

| Método | Descrição |
|--------|-----------|
| `setUsuario(Usuario u)` | Define o usuário logado (chamar após autenticar) |
| `getUsuario()` | Retorna o `Usuario` logado ou `null` |
| `isLogado()` | Retorna `true` se há alguém logado |
| `logout()` | Limpa o usuário logado |

---

## 🔒 `com.sge.util.HashUtil`

| Método | Retorno | Descrição |
|--------|---------|-----------|
| `sha256(String texto)` | `String` | Gera hash SHA-256 em hexadecimal |

> O DAO já aplica SHA-256 automaticamente. Você **NÃO precisa** chamar isso.

---

## 👨‍💼 `com.sge.dao.UsuarioDAO` (RF01 + RF02)

```java
UsuarioDAO dao = new UsuarioDAO();
```

| Método | Retorno | Descrição |
|--------|---------|-----------|
| `autenticar(String login, String senha)` | `Usuario` ou `null` | **(RF01)** Valida login. Retorna `null` se errado |
| `listar()` | `List<Usuario>` | Lista todos os usuários |
| `buscarPorId(int id)` | `Usuario` | Busca um usuário por ID |
| `inserir(Usuario u)` | `void` | Cadastra novo usuário (já aplica SHA-256 na senha) |
| `atualizar(Usuario u)` | `void` | Atualiza. Se `u.getSenha()` for vazio, mantém a senha |
| `excluir(int id)` | `void` | Remove o usuário |

### Exemplo rápido:
```java
Usuario u = dao.autenticar("admin", "admin123");
if (u != null) {
    Sessao.setUsuario(u);
    new DashboardView().setVisible(true);
}
```

### Campos do `Usuario`:
- `id` (int) — gerado pelo banco
- `login` (String)
- `senha` (String) — texto puro, DAO aplica SHA-256
- `nome` (String)
- `perfil` (String) — "ADMIN" ou "OPERADOR"
- `ativo` (boolean)

---

## 🏫 `com.sge.dao.TurmaDAO` (RF03)

```java
TurmaDAO dao = new TurmaDAO();
```

| Método | Retorno | Descrição |
|--------|---------|-----------|
| `listar()` | `List<Turma>` | Lista todas as turmas (ordenadas por ano desc) |
| `buscarPorId(int id)` | `Turma` | Busca por ID |
| `inserir(Turma t)` | `void` | Cadastra nova turma |
| `atualizar(Turma t)` | `void` | Atualiza turma existente |
| `excluir(int id)` | `void` | Remove turma (CASCADE: apaga alunos e notas) |

### Campos do `Turma`:
- `id` (int)
- `nome` (String)
- `descricao` (String) — pode ser vazio
- `ano` (int) — exemplo: 2026

---

## 🎓 `com.sge.dao.AlunoDAO` (RF04)

```java
AlunoDAO dao = new AlunoDAO();
```

| Método | Retorno | Descrição |
|--------|---------|-----------|
| `listar()` | `List<Aluno>` | Lista todos os alunos (com nome da turma preenchido) |
| `listarPorTurma(int turmaId)` | `List<Aluno>` | **(RF07)** Filtra alunos de uma turma específica |
| `buscarPorId(int id)` | `Aluno` | Busca por ID (com nome da turma) |
| `inserir(Aluno a)` | `void` | Cadastra aluno vinculado a uma turma |
| `atualizar(Aluno a)` | `void` | Atualiza aluno |
| `excluir(int id)` | `void` | Remove aluno (CASCADE: apaga notas) |

### Campos do `Aluno`:
- `id` (int)
- `nome` (String)
- `matricula` (String) — única, ex: "ALU001"
- `turmaId` (int) — FK obrigatória
- `turmaNome` (String) — **somente leitura**, preenchido pelo DAO

---

## 📊 `com.sge.dao.NotaDAO` (RF05 + RF07)

```java
NotaDAO dao = new NotaDAO();
```

| Método | Retorno | Descrição |
|--------|---------|-----------|
| `listarPorAluno(int alunoId)` | `List<Nota>` | **(RF05)** Notas de um aluno (com nome preenchido) |
| `listarTodos()` | `List<Nota>` | Todas as notas (para listagem geral) |
| `inserir(Nota n)` | `void` | Lança nova nota |
| `atualizar(Nota n)` | `void` | Edita nota existente |
| `excluir(int id)` | `void` | Remove nota |
| `mediaGeralAluno(int alunoId)` | `double` | **(RF07)** Média geral de um aluno (0 a 10) |

### Campos do `Nota`:
- `id` (int)
- `alunoId` (int)
- `alunoNome` (String) — **somente leitura**
- `disciplina` (String) — ex: "Lógica de Programação"
- `valor` (BigDecimal) — entre 0 e 10 (constraint do banco)
- `bimestre` (int) — 1, 2, 3 ou 4 (constraint do banco)

---

## 📜 `com.sge.dao.LogDAO` (Desafio Extra)

```java
LogDAO dao = new LogDAO();
```

| Método | Retorno | Descrição |
|--------|---------|-----------|
| `listar(int limite)` | `List<Log>` | Lista os últimos N logs (mais recentes primeiro) |
| `registrar(Integer usuarioId, String usuarioLogin, String acao, String tabela, Integer registroId, String detalhes)` | `void` | Insere log manual (use para LOGIN/LOGOUT) |

> 💡 Os INSERT/UPDATE/DELETE em usuarios, turmas, alunos e notas geram logs **automaticamente via Triggers**. Você só precisa registrar LOGIN/LOGOUT manualmente.

---

## ⚠️ Exceções

Todos os métodos dos DAOs podem lançar `java.sql.SQLException`. Sempre use try/catch:

```java
try {
    dao.inserir(turma);
    JOptionPane.showMessageDialog(this, "Cadastrado com sucesso!");
} catch (SQLException ex) {
    JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(),
        "Falha", JOptionPane.ERROR_MESSAGE);
}
```

---

## 🧪 Testando sem GUI (rápido)

Você pode testar os DAOs direto no `main()` de qualquer classe:

```java
public static void main(String[] args) {
    try {
        List<Turma> turmas = new TurmaDAO().listar();
        for (Turma t : turmas) {
            System.out.println(t.getId() + " - " + t.getNome());
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}
```

Roda com **Shift+F6** no NetBeans.
