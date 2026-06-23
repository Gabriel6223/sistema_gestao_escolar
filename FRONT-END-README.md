# 🎨 Guia do Front-End — Como criar as telas no NetBeans

> **Para:** Integrantes do grupo responsáveis pelas telas (Swing)
> **Pré-requisito:** A parte de banco + DAOs já está pronta (commitada pelo Gabriel)

---

## 📋 Visão geral — O que VOCÊ precisa fazer

| Camada | Responsável | Status |
|--------|-------------|--------|
| Banco de dados (MySQL) | Gabriel | ✅ Pronto |
| DAOs (acesso a dados) | Gabriel | ✅ Pronto |
| Classes utilitárias (Conexao, HashUtil, Sessao) | Gabriel | ✅ Pronto |
| Classes de modelo (Usuario, Turma, Aluno, Nota) | Gabriel | ✅ Pronto |
| **`Main.java` (entrada do sistema)** | **VOCÊ** | 🔨 A fazer |
| **`view/*.java` (telas Swing)** | **VOCÊ** | 🔨 A fazer |

> Sua missão: criar a **interface gráfica** em Swing usando o NetBeans, consumindo os DAOs que o Gabriel já preparou.

---

## 🛠️ PARTE 1 — Preparando o ambiente (faça uma vez)

### Passo 1.1 — Instalar o NetBeans

1. Baixe o **NetBeans IDE 17+** (ou a versão mais recente) em:
   👉 <https://netbeans.apache.org/download/>
2. Durante a instalação, marque os plugins:
   - ✅ **Java SE**
   - ✅ **Maven** (importantíssimo!)
3. Instale o **JDK 11 ou superior** se ainda não tiver:
   👉 <https://adoptium.net/>

### Passo 1.2 — Clonar o repositório do projeto

Abra o terminal (ou Git Bash) e rode:

```bash
cd Desktop
git clone https://github.com/Gabriel6223/sistema_gestao_escolar.git
cd sistema_gestao_escolar
```

> 📁 Isso cria a pasta `sistema_gestao_escolar/` com tudo que o Gabriel já commitou.

### Passo 1.3 — Configurar o MySQL

Você precisa do MySQL rodando na sua máquina. Se não tiver:

1. Instale o **MySQL 8.x** (ou **MariaDB 10.5+**):
   - Windows: <https://dev.mysql.com/downloads/installer/>
   - Ou use **XAMPP** (mais fácil): <https://www.apachefriends.org/>

2. Crie o banco de dados rodando o script:
   ```bash
   mysql -u root -p < sql/script.sql
   ```
   Senha padrão do XAMPP para `root` é vazia (só aperte Enter).

3. **Teste se funcionou:**
   ```bash
   mysql -u root -p -e "USE sge; SHOW TABLES;"
   ```
   Deve listar: `alunos`, `logs`, `notas`, `turmas`, `usuarios`.

### Passo 1.4 — Criar usuário dedicado (recomendado)

Para não usar `root` no projeto:

```sql
CREATE USER 'sge'@'localhost' IDENTIFIED BY 'sge123';
GRANT ALL PRIVILEGES ON sge.* TO 'sge'@'localhost';
FLUSH PRIVILEGES;
```

> Anote: usuário `sge`, senha `sge123`. Vai usar isso no Passo 2.

---

## 🚀 PARTE 2 — Abrindo o projeto no NetBeans

### Passo 2.1 — Abrir o projeto

1. Abra o **NetBeans**
2. Menu **File → Open Project...** (`Ctrl + Shift + O`)
3. Navegue até `Desktop/sistema_gestao_escolar/`
4. Selecione a pasta e clique **Open Project**
5. O NetBeans vai reconhecer como **projeto Maven** (ícone com "M")

> 💡 Se aparecer "Maven needs to download dependencies", clique em **Resolve**. Ele vai baixar o driver MySQL sozinho.

### Passo 2.2 — Configurar a conexão com o banco

Abra o arquivo `src/main/java/com/sge/util/Conexao.java` e ajuste:

```java
private static final String URL    = "jdbc:mysql://localhost:3306/sge?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=utf8";
private static final String USER   = "sge";          // ← ajuste aqui
private static final String PASSWORD = "sge123";     // ← ajuste aqui
```

**OU** use variáveis de ambiente (mais seguro):
- No NetBeans: clique direito no projeto → **Properties → Run → Environment Variables**
- Adicione: `SGE_DB_USER=sge`, `SGE_DB_PASSWORD=sge123`

### Passo 2.3 — Testar se tudo funciona

1. **Compile o projeto:**
   - Clique direito no projeto → **Build** (ou pressione `F11`)
   - Deve aparecer `BUILD SUCCESS` no console

2. **Rode o teste de integração (do Gabriel):**
   - Abra `src/test/java/IntegrationTest.java`
   - Clique direito → **Run File** (ou `Shift + F6`)
   - **Esperado:** `12 / 12 testes passaram` ✅

3. **Rode a aplicação:**
   - Clique direito no projeto → **Run** (ou pressione `F6`)
   - Deve abrir a tela de **Login** do Gabriel
   - Entre com: **admin / admin123**
   - Vai aparecer um **erro** porque ainda não criamos as telas — normal!

> ⚠️ O `Main.java` ainda só abre o Login. Você vai criar as outras telas.

---

## 📂 PARTE 3 — Entendendo o que já existe

### Estrutura do projeto no NetBeans

```
sistema_gestao_escolar (projeto Maven)
│
├── src/main/java/com/sge/        ← código fonte
│   ├── Main.java                  ← PONTO DE ENTRADA (você vai editar)
│   ├── model/                     ← POJOs (já prontos)
│   │   ├── Usuario.java
│   │   ├── Turma.java
│   │   ├── Aluno.java
│   │   ├── Nota.java
│   │   └── Log.java
│   ├── dao/                       ← ACESSO AO BANCO (já prontos!)
│   │   ├── UsuarioDAO.java
│   │   ├── TurmaDAO.java
│   │   ├── AlunoDAO.java
│   │   ├── NotaDAO.java
│   │   └── LogDAO.java
│   ├── util/                      ← utilitários (já prontos)
│   │   ├── Conexao.java
│   │   ├── HashUtil.java
│   │   └── Sessao.java
│   └── view/                      ← ⭐ VOCÊ CRIA AS TELAS AQUI
│       └── (vazio por enquanto)
│
├── sql/script.sql                  ← banco de dados (já pronto)
└── pom.xml                         ← dependências Maven (já pronto)
```

### Os 5 DAOs que você vai usar

| DAO | Métodos principais | RF |
|-----|---------------------|----|
| `UsuarioDAO` | `autenticar(login, senha)` → Usuario | RF01 |
| | `listar()`, `inserir(u)`, `atualizar(u)`, `excluir(id)` | RF02 |
| `TurmaDAO` | `listar()`, `buscarPorId(id)`, `inserir(t)`, `atualizar(t)`, `excluir(id)` | RF03 |
| `AlunoDAO` | `listar()`, `listarPorTurma(id)`, `buscarPorId(id)`, `inserir(a)`, `atualizar(a)`, `excluir(id)` | RF04 |
| `NotaDAO` | `listarPorAluno(id)`, `listarTodos()`, `inserir(n)`, `atualizar(n)`, `excluir(id)`, `mediaGeralAluno(id)` | RF05/RF07 |
| `LogDAO` | `listar(limite)`, `registrar(...)` | Desafio Extra |

> 📖 Para detalhes dos métodos, veja o `API.md` na raiz do projeto.

---

## 🎨 PARTE 4 — Padrão visual (use em TODAS as telas)

Para manter o sistema bonito e padronizado, use sempre:

### Cores

```java
// Cor principal (cabeçalhos, botões)
new Color(33, 90, 150)      // azul escuro

// Cor de sucesso (botão "Salvar")
new Color(33, 150, 90)      // verde

// Cor de alerta (botão "Excluir", "Sair")
new Color(180, 60, 60)      // vermelho

// Cor de fundo padrão
new Color(245, 247, 250)    // cinza claro
```

### Fontes

```java
new Font("Segoe UI", Font.BOLD, 18)   // títulos
new Font("Segoe UI", Font.PLAIN, 14)  // campos
new Font("Segoe UI", Font.BOLD, 13)   // botões
```

### Layout padrão de qualquer tela CRUD

```
┌────────────────────────────────────────────────┐
│ [Título da tela (RFxx)]              [Voltar] │ ← Header azul
├──────────────┬─────────────────────────────────┤
│              │                                 │
│  Formulário  │      Tabela com dados           │
│  (lado       │      (lado direito              │
│  esquerdo)   │       ou embaixo)               │
│              │                                 │
│  [Novo]      │                                 │
│  [Salvar]    │                                 │
│  [Excluir]   │                                 │
│  [Limpar]    │                                 │
│              │                                 │
└──────────────┴─────────────────────────────────┘
```

---

## 🧩 PARTE 5 — Como criar uma tela nova (passo a passo)

### Exemplo: criar a tela de **Gestão de Turmas** (RF03)

#### Passo 5.1 — Criar o arquivo

1. No painel **Projects** (esquerda), clique com botão direito em **`com.sge.view`**
2. **New → Java Class...**
3. Nome: `TurmaView`
4. Package: `com.sge.view`
5. Clique **Finish**

#### Passo 5.2 — Estrutura básica (copie este template)

```java
package com.sge.view;

import com.sge.dao.TurmaDAO;       // importa o DAO do Gabriel
import com.sge.model.Turma;        // importa o POJO

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TurmaView extends JFrame {

    // 1. DAO do Gabriel (já funciona!)
    private final TurmaDAO dao = new TurmaDAO();
    
    // 2. Modelo da tabela (não editável)
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nome", "Descrição", "Ano"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    
    // 3. Componentes visuais
    private JTable tabela;
    private JTextField txtId, txtNome, txtDescricao, txtAno;

    // 4. Construtor (monta a tela)
    public TurmaView() {
        setTitle("SGE - Gestão de Turmas");
        setSize(900, 600);
        setLocationRelativeTo(null);  // centraliza
        setLayout(new BorderLayout(10, 10));

        // --- Header azul ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 90, 150));
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel titulo = new JLabel("Gestão de Turmas (RF03)");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(titulo, BorderLayout.WEST);
        JButton voltar = new JButton("Voltar");
        voltar.addActionListener(e -> {
            // Volta para o Dashboard (criar depois)
            // new DashboardView().setVisible(true);
            dispose();
        });
        header.add(voltar, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- Formulário à esquerda ---
        add(criarFormulario(), BorderLayout.WEST);
        
        // --- Tabela à direita ---
        add(criarTabela(), BorderLayout.CENTER);

        // Carrega os dados iniciais
        carregar();
    }

    // ... (resto do código vem abaixo)
}
```

#### Passo 5.3 — Adicionar o método `criarFormulario()`

```java
private JPanel criarFormulario() {
    JPanel p = new JPanel(new GridBagLayout());
    p.setBorder(BorderFactory.createTitledBorder("Dados da Turma"));
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(6, 6, 6, 6);
    c.fill = GridBagConstraints.HORIZONTAL;

    txtId = new JTextField();
    txtId.setEditable(false);  // ID é gerado pelo banco
    txtNome = new JTextField(15);
    txtDescricao = new JTextField(15);
    txtAno = new JTextField(String.valueOf(java.time.Year.now().getValue()), 15);

    c.gridx = 0; c.gridy = 0; p.add(new JLabel("ID:"), c); c.gridy++; p.add(txtId, c);
    c.gridy++; p.add(new JLabel("Nome:*"), c); c.gridy++; p.add(txtNome, c);
    c.gridy++; p.add(new JLabel("Descrição:"), c); c.gridy++; p.add(txtDescricao, c);
    c.gridy++; p.add(new JLabel("Ano:*"), c); c.gridy++; p.add(txtAno, c);

    // Botões CRUD
    JButton btnNovo = criarBotao("Novo");
    JButton btnSalvar = criarBotao("Salvar");
    JButton btnExcluir = criarBotaoExcluir();
    JButton btnLimpar = criarBotao("Limpar");

    btnNovo.addActionListener(e -> limpar());
    btnLimpar.addActionListener(e -> limpar());
    btnSalvar.addActionListener(e -> salvar());
    btnExcluir.addActionListener(e -> excluir());

    JPanel botoes = new JPanel(new GridLayout(2, 2, 6, 6));
    botoes.add(btnNovo); botoes.add(btnSalvar);
    botoes.add(btnExcluir); botoes.add(btnLimpar);
    c.gridy++; p.add(botoes, c);

    return p;
}

private JButton criarBotao(String texto) {
    JButton b = new JButton(texto);
    b.setBackground(new Color(33, 90, 150));
    b.setForeground(Color.WHITE);
    b.setFocusPainted(false);
    b.setFont(new Font("Segoe UI", Font.BOLD, 13));
    return b;
}

private JButton criarBotaoExcluir() {
    JButton b = new JButton("Excluir");
    b.setBackground(new Color(180, 60, 60));
    b.setForeground(Color.WHITE);
    b.setFocusPainted(false);
    b.setFont(new Font("Segoe UI", Font.BOLD, 13));
    return b;
}
```

#### Passo 5.4 — Adicionar o método `criarTabela()`

```java
private JScrollPane criarTabela() {
    tabela = new JTable(model);
    tabela.setRowHeight(22);
    tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    // Quando clicar numa linha, preenche o formulário
    tabela.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) carregarSelecionado();
    });
    
    JScrollPane sp = new JScrollPane(tabela);
    sp.setBorder(BorderFactory.createTitledBorder("Turmas cadastradas"));
    return sp;
}
```

#### Passo 5.5 — Métodos de carregar/salvar/excluir (usando o DAO do Gabriel)

```java
// Carrega todas as turmas do banco na tabela
private void carregar() {
    model.setRowCount(0);  // limpa a tabela
    try {
        List<Turma> lista = dao.listar();   // ← método do Gabriel!
        for (Turma t : lista) {
            model.addRow(new Object[]{
                t.getId(), t.getNome(), t.getDescricao(), t.getAno()
            });
        }
    } catch (SQLException ex) {
        mostrarErro(ex);
    }
}

// Quando clica numa linha, preenche os campos
private void carregarSelecionado() {
    int row = tabela.getSelectedRow();
    if (row < 0) return;
    
    try {
        int id = (int) model.getValueAt(row, 0);
        Turma t = dao.buscarPorId(id);  // ← método do Gabriel!
        if (t != null) {
            txtId.setText(String.valueOf(t.getId()));
            txtNome.setText(t.getNome());
            txtDescricao.setText(t.getDescricao());
            txtAno.setText(String.valueOf(t.getAno()));
        }
    } catch (SQLException ex) {
        mostrarErro(ex);
    }
}

// Limpa os campos
private void limpar() {
    txtId.setText("");
    txtNome.setText("");
    txtDescricao.setText("");
    txtAno.setText(String.valueOf(java.time.Year.now().getValue()));
    tabela.clearSelection();
}

// Salva (insere se ID vazio, senão atualiza)
private void salvar() {
    try {
        // Validação
        if (txtNome.getText().isBlank() || txtAno.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Nome e Ano são obrigatórios!");
            return;
        }
        
        // Monta o objeto
        Turma t = new Turma();
        if (!txtId.getText().isBlank()) {
            t.setId(Integer.parseInt(txtId.getText()));
        }
        t.setNome(txtNome.getText().trim());
        t.setDescricao(txtDescricao.getText().trim());
        t.setAno(Integer.parseInt(txtAno.getText().trim()));
        
        // Chama o DAO do Gabriel
        if (t.getId() == 0) {
            dao.inserir(t);   // INSERT
        } else {
            dao.atualizar(t); // UPDATE
        }
        
        carregar();  // atualiza a tabela
        limpar();    // limpa o formulário
    } catch (SQLException ex) {
        mostrarErro(ex);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Ano inválido!");
    }
}

// Exclui a turma selecionada
private void excluir() {
    if (txtId.getText().isBlank()) {
        JOptionPane.showMessageDialog(this, "Selecione uma turma primeiro!");
        return;
    }
    
    int opt = JOptionPane.showConfirmDialog(this,
        "Excluir a turma selecionada?",
        "Confirmação", JOptionPane.YES_NO_OPTION);
    
    if (opt == JOptionPane.YES_OPTION) {
        try {
            dao.excluir(Integer.parseInt(txtId.getText()));  // ← método do Gabriel!
            carregar();
            limpar();
        } catch (SQLException ex) {
            mostrarErro(ex);
        }
    }
}

// Mostra erros em popup
private void mostrarErro(SQLException ex) {
    JOptionPane.showMessageDialog(this,
        "Erro: " + ex.getMessage(),
        "Falha na operação",
        JOptionPane.ERROR_MESSAGE);
}
```

#### Passo 5.6 — Compilar e rodar

1. Pressione **F11** (Build) — não deve ter erros
2. Para **rodar** essa tela específica:
   - Clique direito em `TurmaView.java` → **Run File** (`Shift + F6`)
3. Para abrir do `Main.java`, altere temporariamente o `Main.java`:

```java
package com.sge;

import com.sge.view.TurmaView;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new TurmaView().setVisible(true));
    }
}
```

---

## 📝 PARTE 6 — Receitas prontas (copie e adapte)

### 🔹 RF02 — Gestão de Usuários (com senha SHA-256)

```java
import com.sge.dao.UsuarioDAO;
import com.sge.model.Usuario;
import com.sge.util.HashUtil;  // importante pra senha!

// No método salvar():
Usuario u = new Usuario();
if (!txtId.getText().isBlank()) u.setId(Integer.parseInt(txtId.getText()));
u.setLogin(txtLogin.getText().trim());
u.setNome(txtNome.getText().trim());
if (!txtSenha.getText().isBlank()) {
    u.setSenha(txtSenha.getText());  // DAO aplica SHA-256 sozinho
}
u.setPerfil((String) cbPerfil.getSelectedItem());
u.setAtivo(chkAtivo.isSelected());

if (u.getId() == 0) dao.inserir(u); else dao.atualizar(u);
```

### 🔹 RF04 — Alunos (com turma no combo)

```java
import com.sge.dao.AlunoDAO;
import com.sge.dao.TurmaDAO;
import com.sge.model.Aluno;
import com.sge.model.Turma;
import java.util.HashMap;
import java.util.Map;

private JComboBox<Turma> cbTurma;
private final Map<Integer, String> mapaTurmas = new HashMap<>();

// Carrega as turmas no combo:
private void carregarTurmas() {
    cbTurma.removeAllItems();
    try {
        List<Turma> turmas = new TurmaDAO().listar();
        for (Turma t : turmas) {
            mapaTurmas.put(t.getId(), t.getNome());
            cbTurma.addItem(t);
        }
    } catch (SQLException ex) { mostrarErro(ex); }
}

// No salvar:
Aluno a = new Aluno();
if (!txtId.getText().isBlank()) a.setId(Integer.parseInt(txtId.getText()));
a.setNome(txtNome.getText().trim());
a.setMatricula(txtMatricula.getText().trim());
Turma t = (Turma) cbTurma.getSelectedItem();
a.setTurmaId(t.getId());

if (a.getId() == 0) alunoDao.inserir(a); else alunoDao.atualizar(a);
```

### 🔹 RF05 — Notas (quantidade dinâmica)

O truque aqui é: o usuário pode adicionar **várias notas por aluno**. Use um diálogo:

```java
private void abrirDialogNovaNota(int alunoId) {
    JTextField txtDisciplina = new JTextField();
    JComboBox<Integer> cbBimestre = new JComboBox<>(new Integer[]{1, 2, 3, 4});
    JTextField txtValor = new JTextField();

    JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
    form.add(new JLabel("Disciplina:")); form.add(txtDisciplina);
    form.add(new JLabel("Bimestre:"));   form.add(cbBimestre);
    form.add(new JLabel("Valor (0-10):")); form.add(txtValor);

    int res = JOptionPane.showConfirmDialog(this, form,
        "Nova Nota", JOptionPane.OK_CANCEL_OPTION);

    if (res == JOptionPane.OK_OPTION) {
        try {
            Nota n = new Nota();
            n.setAlunoId(alunoId);
            n.setDisciplina(txtDisciplina.getText().trim());
            n.setBimestre((Integer) cbBimestre.getSelectedItem());
            n.setValor(new BigDecimal(txtValor.getText().replace(",", ".")));
            notaDao.inserir(n);
            carregar();  // atualiza a tabela
        } catch (Exception ex) { mostrarErro((SQLException) ex); }
    }
}
```

### 🔹 RF06 — Dashboard (Menu Principal)

Tela com botões grandes que abrem as outras telas:

```java
public DashboardView() {
    setTitle("SGE - Painel Principal");
    setSize(900, 600);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    // Header
    JPanel header = new JPanel(new BorderLayout());
    header.setBackground(new Color(33, 90, 150));
    // ... título + nome do usuário logado

    // Grid 3x3 com cards de acesso rápido
    JPanel center = new JPanel(new GridLayout(3, 3, 18, 18));
    center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    center.setBackground(new Color(245, 247, 250));

    center.add(criarCard("Usuários", "RF02", e -> new UsuarioView().setVisible(true)));
    center.add(criarCard("Turmas", "RF03", e -> new TurmaView().setVisible(true)));
    center.add(criarCard("Alunos", "RF04", e -> new AlunoView().setVisible(true)));
    center.add(criarCard("Notas", "RF05", e -> new NotaView().setVisible(true)));
    center.add(criarCard("Boletim", "RF07", e -> new BoletimView().setVisible(true)));
    center.add(criarCard("Logs", "Extra", e -> new LogView().setVisible(true)));

    add(header, BorderLayout.NORTH);
    add(center, BorderLayout.CENTER);

    // Botão de Logout no rodapé
    // ...
}

private JButton criarCard(String titulo, String sub, ActionListener acao) {
    JButton b = new JButton("<html><center><b style='font-size:14px'>" + titulo +
            "</b><br><span style='font-size:11px'>" + sub + "</span></center></html>");
    b.setBackground(Color.WHITE);
    b.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
        BorderFactory.createEmptyBorder(20, 10, 20, 10)));
    b.addActionListener(acao);
    return b;
}
```

### 🔹 RF07 — Boletim com média calculada

Use o método `mediaGeralAluno(id)` do `NotaDAO` (do Gabriel):

```java
// BoletimView.java - método gerar()
private void gerar() {
    Aluno a = (Aluno) cbAluno.getSelectedItem();
    if (a == null) return;
    
    try {
        List<Nota> notas = notaDao.listarPorAluno(a.getId());
        
        // Agrupa por disciplina e calcula médias
        Map<String, List<BigDecimal>> porDisciplina = new LinkedHashMap<>();
        for (Nota n : notas) {
            porDisciplina.computeIfAbsent(n.getDisciplina(), k -> new ArrayList<>())
                        .add(n.getValor());
        }
        
        BigDecimal somaMedias = BigDecimal.ZERO;
        int qtd = 0;
        
        for (var entry : porDisciplina.entrySet()) {
            BigDecimal soma = entry.getValue().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal media = soma.divide(new BigDecimal(entry.getValue().size()), 2, RoundingMode.HALF_UP);
            model.addRow(new Object[]{entry.getKey(), media.toString()});
            somaMedias = somaMedias.add(media);
            qtd++;
        }
        
        BigDecimal mediaGeral = qtd > 0
            ? somaMedias.divide(new BigDecimal(qtd), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        
        lblMediaGeral.setText("Média Geral: " + mediaGeral.toString());
    } catch (SQLException ex) { mostrarErro(ex); }
}
```

---

## 🧪 PARTE 7 — Como testar sua tela

### Teste manual (sem código)

1. Rode a aplicação (`F6`)
2. Faça login: **admin / admin123**
3. Teste **cada botão** da sua tela:
   - Novo (limpar campos) ✅
   - Salvar (cadastrar novo) ✅
   - Clicar na linha → campos preenchem ✅
   - Salvar de novo (atualizar) ✅
   - Excluir ✅
4. Verifique no MySQL se os dados foram realmente gravados:
   ```sql
   USE sge;
   SELECT * FROM turmas;  -- (ou outra tabela)
   ```

### Teste automatizado (do Gabriel)

Sempre que terminar uma tela, rode o `IntegrationTest` para garantir que você não quebrou nada:

```bash
cd sistema_gestao_escolar
mvn test
```

Esperado: **`12 / 12 testes passaram`** ✅

---

## 📤 PARTE 8 — Como enviar seu trabalho para o GitHub

### Workflow básico (repita para cada tela que criar)

```bash
cd ~/Desktop/sistema_gestao_escolar

# 1. Antes de tudo, baixe as atualizações do time
git pull origin master

# 2. Crie/edite seus arquivos no NetBeans...

# 3. Veja o que mudou
git status

# 4. Adicione SUAS telas (não mexe em arquivos dos outros!)
git add src/main/java/com/sge/view/SuaTela.java
git add src/main/java/com/sge/Main.java

# 5. Commit com mensagem descritiva
git commit -m "feat(view): cria tela de gestão de turmas (RF03)"

# 6. Envie pro GitHub
git push origin master
```

### ⚠️ Regras de ouro do trabalho em equipe

1. **Nunca commite direto na master do Gabriel sem avisar** — sempre faça um branch:
   ```bash
   git checkout -b feat/tela-turmas
   # ... trabalha ...
   git add . && git commit -m "feat: tela de turmas"
   git push origin feat/tela-turmas
   # Avisa o Gabriel pra fazer merge
   ```

2. **Não altere os DAOs!** São do Gabriel. Se precisar de algo novo nele, peça pra ele.

3. **Não altere `Conexao.java`** sem combinar — afeta todo o time.

4. **Cada commit seu deve ter contribuição real** (o professor vai olhar o histórico!).

---

## ✅ PARTE 9 — Checklist de Entrega (por integrante)

### Pessoa responsável pelo Login + Dashboard (RF01 + RF06)

- [ ] `LoginView.java` com autenticação (admin/admin123)
- [ ] `DashboardView.java` com menu de botões
- [ ] Botão **Logout** funcionando
- [ ] Atualizar `Main.java` para abrir o Login

### Pessoa responsável pelos CRUDs (RF02 + RF03 + RF04)

- [ ] `UsuarioView.java` (CRUD completo)
- [ ] `TurmaView.java` (CRUD completo)
- [ ] `AlunoView.java` (com combo de Turmas)

### Pessoa responsável por Notas + Boletim (RF05 + RF07)

- [ ] `NotaView.java` (lançamento dinâmico)
- [ ] `BoletimView.java` (com média calculada)
- [ ] Telas de listagem (RF07): `UsuarioListagemView`, `TurmaListagemView`, `AlunoListagemView`

### Pessoa responsável pelo Desafio Extra (Logs)

- [ ] `LogView.java` (consultar histórico de auditoria)

---

## 🆘 Problemas comuns

| Erro | Solução |
|------|---------|
| `cannot find symbol: class UsuarioDAO` | Você não fez `import com.sge.dao.UsuarioDAO;` no topo do arquivo |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Maven não baixou as dependências → clique direito no projeto → **Build with Dependencies** |
| `Access denied for user 'root'@'localhost'` | Ajuste usuário/senha em `Conexao.java` |
| `Unknown database 'sge'` | Rode `script.sql` primeiro |
| `cannot resolve symbol Turma` | Você não importou: `import com.sge.model.Turma;` |

---

## 💬 Comunicação com o Gabriel (sua parte de banco)

Se você precisar de algo novo no DAO, peça assim:

> *"Gabriel, preciso de um método no `AlunoDAO` que retorne alunos com a média calculada. Pode adicionar?"*

Ele vai adicionar e te avisar quando der `push`. Aí você roda:

```bash
git pull origin master
```

E o novo método estará disponível pra você usar.

---

## 🚀 Resumo rápido do workflow diário

```bash
# 1. Começar o dia
cd ~/Desktop/sistema_gestao_escolar
git pull origin master

# 2. Trabalhar no NetBeans...

# 3. Antes de ir embora
git add src/main/java/com/sge/view/
git commit -m "feat(view): [descreva o que fez]"
git push origin master
```

Boa sorte! 🎨
