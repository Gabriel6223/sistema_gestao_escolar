package com.sge.dao;

import com.sge.model.Nota;
import com.sge.util.Conexao;
import com.sge.util.Sessao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NotaDAO {

    private void setContextoApp(Connection conn) throws SQLException {
        if (Sessao.isLogado()) {
            try (PreparedStatement ps = conn.prepareStatement("SET @app_user_id = ?, @app_user_login = ?")) {
                ps.setInt(1, Sessao.getUsuario().getId());
                ps.setString(2, Sessao.getUsuario().getLogin());
                ps.execute();
            }
        }
    }

    public List<Nota> listarPorAluno(int alunoId) throws SQLException {
        List<Nota> lista = new ArrayList<>();
        String sql = "SELECT n.id, n.aluno_id, n.disciplina, n.valor, n.bimestre, n.created_at, a.nome AS aluno_nome " +
                     "FROM notas n JOIN alunos a ON a.id = n.aluno_id " +
                     "WHERE n.aluno_id = ? ORDER BY n.disciplina, n.bimestre";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, alunoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Nota n = new Nota();
                    n.setId(rs.getInt("id"));
                    n.setAlunoId(rs.getInt("aluno_id"));
                    n.setAlunoNome(rs.getString("aluno_nome"));
                    n.setDisciplina(rs.getString("disciplina"));
                    n.setValor(rs.getBigDecimal("valor"));
                    n.setBimestre(rs.getInt("bimestre"));
                    n.setCreatedAt(rs.getTimestamp("created_at"));
                    lista.add(n);
                }
            }
        }
        return lista;
    }

    public List<Nota> listarTodos() throws SQLException {
        List<Nota> lista = new ArrayList<>();
        String sql = "SELECT n.id, n.aluno_id, n.disciplina, n.valor, n.bimestre, n.created_at, a.nome AS aluno_nome " +
                     "FROM notas n JOIN alunos a ON a.id = n.aluno_id ORDER BY a.nome, n.disciplina";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Nota n = new Nota();
                n.setId(rs.getInt("id"));
                n.setAlunoId(rs.getInt("aluno_id"));
                n.setAlunoNome(rs.getString("aluno_nome"));
                n.setDisciplina(rs.getString("disciplina"));
                n.setValor(rs.getBigDecimal("valor"));
                n.setBimestre(rs.getInt("bimestre"));
                n.setCreatedAt(rs.getTimestamp("created_at"));
                lista.add(n);
            }
        }
        return lista;
    }

    public void inserir(Nota n) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            setContextoApp(conn);
            String sql = "INSERT INTO notas (aluno_id, disciplina, valor, bimestre) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, n.getAlunoId());
                ps.setString(2, n.getDisciplina());
                ps.setBigDecimal(3, n.getValor());
                ps.setInt(4, n.getBimestre());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) n.setId(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Nota n) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            setContextoApp(conn);
            String sql = "UPDATE notas SET disciplina = ?, valor = ?, bimestre = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, n.getDisciplina());
                ps.setBigDecimal(2, n.getValor());
                ps.setInt(3, n.getBimestre());
                ps.setInt(4, n.getId());
                ps.executeUpdate();
            }
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            setContextoApp(conn);
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM notas WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
    }

    /** Média geral de um aluno (todas as notas, todas as disciplinas). */
    public double mediaGeralAluno(int alunoId) throws SQLException {
        String sql = "SELECT AVG(valor) FROM notas WHERE aluno_id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, alunoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        }
        return 0.0;
    }
}
