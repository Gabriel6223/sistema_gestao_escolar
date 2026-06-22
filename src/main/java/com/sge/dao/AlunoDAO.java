package com.sge.dao;

import com.sge.model.Aluno;
import com.sge.util.Conexao;
import com.sge.util.Sessao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {

    private void setContextoApp(Connection conn) throws SQLException {
        if (Sessao.isLogado()) {
            try (PreparedStatement ps = conn.prepareStatement("SET @app_user_id = ?, @app_user_login = ?")) {
                ps.setInt(1, Sessao.getUsuario().getId());
                ps.setString(2, Sessao.getUsuario().getLogin());
                ps.execute();
            }
        }
    }

    public List<Aluno> listar() throws SQLException {
        return listarPorTurma(0);
    }

    public List<Aluno> listarPorTurma(int turmaId) throws SQLException {
        List<Aluno> lista = new ArrayList<>();
        String sql = "SELECT a.id, a.nome, a.matricula, a.turma_id, a.created_at, t.nome AS turma_nome " +
                     "FROM alunos a JOIN turmas t ON t.id = a.turma_id " +
                     (turmaId > 0 ? "WHERE a.turma_id = ? " : "") +
                     "ORDER BY t.nome, a.nome";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (turmaId > 0) ps.setInt(1, turmaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Aluno a = new Aluno();
                    a.setId(rs.getInt("id"));
                    a.setNome(rs.getString("nome"));
                    a.setMatricula(rs.getString("matricula"));
                    a.setTurmaId(rs.getInt("turma_id"));
                    a.setTurmaNome(rs.getString("turma_nome"));
                    a.setCreatedAt(rs.getTimestamp("created_at"));
                    lista.add(a);
                }
            }
        }
        return lista;
    }

    public Aluno buscarPorId(int id) throws SQLException {
        String sql = "SELECT a.id, a.nome, a.matricula, a.turma_id, t.nome AS turma_nome " +
                     "FROM alunos a JOIN turmas t ON t.id = a.turma_id WHERE a.id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Aluno a = new Aluno();
                    a.setId(rs.getInt("id"));
                    a.setNome(rs.getString("nome"));
                    a.setMatricula(rs.getString("matricula"));
                    a.setTurmaId(rs.getInt("turma_id"));
                    a.setTurmaNome(rs.getString("turma_nome"));
                    return a;
                }
            }
        }
        return null;
    }

    public void inserir(Aluno a) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            setContextoApp(conn);
            String sql = "INSERT INTO alunos (nome, matricula, turma_id) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, a.getNome());
                ps.setString(2, a.getMatricula());
                ps.setInt(3, a.getTurmaId());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) a.setId(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Aluno a) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            setContextoApp(conn);
            String sql = "UPDATE alunos SET nome = ?, matricula = ?, turma_id = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, a.getNome());
                ps.setString(2, a.getMatricula());
                ps.setInt(3, a.getTurmaId());
                ps.setInt(4, a.getId());
                ps.executeUpdate();
            }
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            setContextoApp(conn);
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM alunos WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
    }
}
