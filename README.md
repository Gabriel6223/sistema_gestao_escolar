# API dos DAOs (para o time de Front-end)

## Conexao
- `Conexao.getConnection()` → retorna `Connection` JDBC
- Configurar URL/user/senha em `Conexao.java`

## Sessao
- `Sessao.setUsuario(Usuario)` / `Sessao.getUsuario()` / `Sessao.logout()`

## HashUtil
- `HashUtil.sha256(String)` → retorna hash SHA-256 em hex

## UsuarioDAO
- `autenticar(String login, String senha) → Usuario | null`  (RF01)
- `listar() → List<Usuario>`                                 (RF07)
- `buscarPorId(int) → Usuario`
- `inserir(Usuario)` / `atualizar(Usuario)` / `excluir(int)` (RF02)

## TurmaDAO
- `listar() → List<Turma>`
- `buscarPorId(int) → Turma`
- `inserir(Turma)` / `atualizar(Turma)` / `excluir(int)`     (RF03)

## AlunoDAO
- `listar() → List<Aluno>` (com `turmaNome` preenchido)
- `listarPorTurma(int turmaId) → List<Aluno>`                (RF07)
- `buscarPorId(int) → Aluno`
- `inserir(Aluno)` / `atualizar(Aluno)` / `excluir(int)`     (RF04)

## NotaDAO
- `listarPorAluno(int alunoId) → List<Nota>`
- `listarTodos() → List<Nota>`
- `inserir(Nota)` / `atualizar(Nota)` / `excluir(int)`       (RF05)
- `mediaGeralAluno(int alunoId) → double`                    (RF07)

## LogDAO
- `listar(int limite) → List<Log>`                           (Desafio Extra)
- `registrar(Integer usuarioId, String login, String acao, String tabela, Integer registroId, String detalhes)`