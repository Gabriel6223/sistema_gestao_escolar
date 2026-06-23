# SGE – Sistema de Gestão Escolar

Aplicação **desktop** em **Java Swing** com banco de dados **MySQL**, desenvolvida como projeto final da disciplina de **Programação Orientada a Objetos**.

Permite gerenciar **usuários**, **turmas**, **alunos** e o **lançamento dinâmico de notas**, com geração de **boletins** e cálculo automático de **média final**. Inclui também um **sistema de auditoria (logs)** implementado com *Triggers* no banco de dados.

---

## 🚀 Funcionalidades (Requisitos Funcionais)

| Código  | Funcionalidade                                  | Status |
| ------- | ----------------------------------------------- | :----: |
| RF01    | Autenticação (Login restrito)                   |   ✅   |
| RF02    | CRUD completo de Usuários                       |   ✅   |
| RF03    | Cadastro e listagem de Turmas                   |   ✅   |
| RF04    | Cadastro de Alunos com vínculo à Turma          |   ✅   |
| RF05    | Lançamento dinâmico de Notas                    |   ✅   |
| RF06    | Dashboard / Menu Principal com Logout           |   ✅   |
| RF07    | Relatórios (usuários, turmas, alunos, boletim)  |   ✅   |
| Extra   | Sistema de Logs com Triggers + tela de auditoria |   ✅   |

---

## 🛠️ Tecnologias

- **Java 11+**
- **Swing** (interface gráfica)
- **MySQL 8.x** (banco de dados)
- **JDBC** (acesso a dados)
- **Maven** (build e dependências)
- **Git / GitHub** (controle de versão)

---

## 📂 Estrutura do projeto

```
sge/
├── sql/
│   └── script.sql                       # Script de criação do banco (DDL + Triggers + dados de exemplo)
├── src/main/java/com/sge/
│   ├── Main.java                        # Ponto de entrada
│   ├── model/                           # Classes de modelo (POJOs)
│   │   ├── Usuario.java
│   │   ├── Turma.java
│   │   ├── Aluno.java
│   │   ├── Nota.java
│   │   └── Log.java
│   ├── dao/                             # Classes de acesso a dados
│   │   ├── UsuarioDAO.java
│   │   ├── TurmaDAO.java
│   │   ├── AlunoDAO.java
│   │   ├── NotaDAO.java
│   │   └── LogDAO.java
│   ├── view/                            # Telas Swing
│   │   ├── LoginView.java               # RF01
│   │   ├── DashboardView.java           # RF06
│   │   ├── UsuarioView.java             # RF02
│   │   ├── TurmaView.java               # RF03
│   │   ├── AlunoView.java               # RF04
│   │   ├── NotaView.java                # RF05
│   │   ├── BoletimView.java             # RF07
│   │   ├── LogView.java                 # Desafio Extra
│   │   ├── UsuarioListagemView.java     # RF07
│   │   ├── TurmaListagemView.java       # RF07
│   │   └── AlunoListagemView.java       # RF07
│   └── util/                            # Utilitários
│       ├── Conexao.java                 # Gerencia a conexão JDBC
│       ├── Sessao.java                  # Usuário logado em memória
│       └── HashUtil.java                # Hash SHA-256 de senhas
├── pom.xml                              # Maven (driver MySQL + JAR executável)
└── README.md
```

---

## ▶️ Como rodar o projeto (passo a passo)

### 1. Pré-requisitos

- **Java JDK 11 ou superior** instalado (`java -version`)
- **MySQL 8.x** instalado e em execução
- (Opcional) **Maven 3.6+** — se não tiver, veja "Rodando sem Maven" abaixo

### 2. Criar o banco de dados

Abra o terminal MySQL (ou use MySQL Workbench / DBeaver) e execute:

```bash
mysql -u root -p < sql/script.sql
```

Isso criará o banco `sge` com todas as tabelas, *triggers*, e o usuário padrão:

> **Login:** `admin`
> **Senha:** `admin123`

### 3. Configurar a conexão

Edite o arquivo `src/main/java/com/sge/util/Conexao.java` se o seu MySQL exigir usuário/senha diferentes:

```java
private static final String URL    = "jdbc:mysql://localhost:3306/sge?...";
private static final String USER   = "root";
private static final String PASSWORD = "";   // sua senha aqui
```

### 4. Compilar e executar

#### ✅ Com Maven (recomendado)

```bash
# Na raiz do projeto (onde está o pom.xml):
mvn clean package
java -jar target/sge-1.0.0.jar
```

#### ✅ Sem Maven (manual)

Baixe o **driver JDBC do MySQL** (`mysql-connector-java-8.x.jar`) em:
<https://dev.mysql.com/downloads/connector/j/>

```bash
# Linux / macOS
javac -cp ".:lib/mysql-connector-java-8.0.33.jar" -d out $(find src -name "*.java")
java  -cp "out:lib/mysql-connector-java-8.0.33.jar" com.sge.Main

# Windows (PowerShell)
javac -cp ".;lib\mysql-connector-java-8.0.33.jar" -d out (Get-ChildItem -Recurse src\*.java)
java  -cp "out;lib\mysql-connector-java-8.0.33.jar" com.sge.Main
```

### 5. Primeiro acesso

1. Abra a aplicação → tela de **Login**
2. Informe `admin` / `admin123`
3. Você será direcionado ao **Dashboard** com acesso a todas as funcionalidades
4. Use o botão **Logout** para encerrar a sessão

---

## 🧠 Modelagem de Dados

```
usuarios (1) ──── (N) logs
turmas    (1) ──── (N) alunos (1) ──── (N) notas
```

- **usuarios** → administradores com acesso ao sistema (login + senha SHA-256)
- **turmas** → cursos / agrupamentos (ex.: "Jovem Programador", "Técnico em Administração")
- **alunos** → vinculados **obrigatoriamente** a uma turma (FK + ON DELETE CASCADE)
- **notas** → múltiplas notas por aluno (disciplina, bimestre 1–4, valor 0–10)
- **logs** → auditoria populada por **Triggers** + registros manuais (LOGIN/LOGOUT)

> Cada operação de INSERT/UPDATE/DELETE em `usuarios`, `turmas`, `alunos` e `notas` gera automaticamente um registro em `logs`. A aplicação injeta o usuário logado nas variáveis de sessão `@app_user_id` e `@app_user_login` antes de cada operação, e as *Triggers* leem essas variáveis para registrar quem executou a ação.

---

## 🎯 Roteiro de uso (para demonstração ao professor)

1. **Login** com `admin / admin123`
2. **Cadastrar turma** (RF03) — *Turmas*
3. **Cadastrar aluno** (RF04) — *Alunos* (vinculando à turma criada)
4. **Lançar notas** (RF05) — *Notas* (múltiplas notas por aluno, em bimestres diferentes)
5. **Gerar boletim** (RF07) — *Boletim* (médias por disciplina + média geral)
6. **Conferir auditoria** (Extra) — *Logs* (todas as operações aparecem registradas)

---

## 👥 Trabalho em equipe

> A participação individual é verificada pelo **histórico de commits no GitHub**. Todos os integrantes devem contribuir com código real (features, correções, melhorias, testes).

### Sugestão de divisão de tarefas (3–5 pessoas)

| Membro       | Sugestão de contribuição                                       |
| ------------ | -------------------------------------------------------------- |
| Pessoa 1     | Modelagem do banco + Triggers (script.sql)                     |
| Pessoa 2     | DAO + conexão JDBC + utilitários                               |
| Pessoa 3     | Telas Swing (RF02, RF03, RF04)                                 |
| Pessoa 4     | Telas Swing (RF05, RF06, RF07)                                 |
| Pessoa 5     | Login + Logs (Desafio Extra) + polimento / README              |

> Cada membro faz **commits frequentes** e **descritivos**.

---

## 📦 Entregáveis (Checklist)

- [x] Repositório GitHub criado e compartilhado
- [x] Script DDL (`sql/script.sql`) no repositório
- [x] Todos os RFs (RF01–RF07) implementados
- [x] Desafio Extra (Logs com Triggers + tela de consulta)
- [x] README com instruções completas
- [x] Senhas armazenadas com hash SHA-256
- [x] Tratamento de erros e validações nas telas

---

## 🧪 Testes rápidos

Para confirmar que tudo está funcionando:

1. Logar como `admin / admin123` ✅
2. Cadastrar uma nova turma ✅
3. Cadastrar um aluno vinculado a essa turma ✅
4. Lançar 3 notas em disciplinas/bimestres diferentes ✅
5. Abrir o boletim do aluno → ver as notas e a média geral ✅
6. Abrir "Logs" → ver todas as operações registradas com usuário, ação, tabela e detalhes ✅
7. Logout → voltar para a tela de Login ✅
