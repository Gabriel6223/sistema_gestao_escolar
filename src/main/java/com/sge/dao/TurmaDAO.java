package com.sge.dao;

import com.sge.model.Turma;
import com.sge.util.Conexao;
import com.sge.util.Sessao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TurmaDAO {

    private void setContextoApp(Connection conn) throws SQLException {
        if (Sessao.isLogado()) {
            try (PreparedStatement ps = conn.prepareStatement("SET @app_user_id = ?, @app_user_login = ?")) {
                ps.setInt(1, Sessao.getUsuario().getId());
                ps.setString(2, Sessao.getUsuario().getLogin());
                ps.execute();
            }
        }
    }

    public List<Turma> listar() throws SQLException {
        List<Turma> lista = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, ano, created_at FROM turmas ORDER BY ano DESC, nome";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Turma t = new Turma();
                t.setId(rs.getInt("id"));
                t.setNome(rs.getString("nome"));
                t.setDescricao(rs.getString("descricao"));
                t.setAno(rs.getInt("ano"));
                t.setCreatedAt(rs.getTimestamp("created_at"));
                lista.add(t);
            }
        }
        return lista;
    }

    public Turma buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, descricao, ano, created_at FROM turmas WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Turma t = new Turma();
                    t.setId(rs.getInt("id"));
                    t.setNome(rs.getString("nome"));
                    t.setDescricao(rs.getString("descricao"));
                    t.setAno(rs.getInt("ano"));
                    t.setCreatedAt(rs.getTimestamp("created_at"));
                    return t;
                }
            }
        }
        return null;
    }

    public void inserir(Turma t) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            setContextoApp(conn);
            String sql = "INSERT INTO turmas (nome, descricao, ano) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, t.getNome());
                ps.setString(2, t.getDescricao());
                ps.setInt(3, t.getAno());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) t.setId(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Turma t) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            setContextoApp(conn);
            String sql = "UPDATE turmas SET nome = ?, descricao = ?, ano = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, t.getNome());
                ps.setString(2, t.getDescricao());
                ps.setInt(3, t.getAno());
                ps.setInt(4, t.getId());
                ps.executeUpdate();
            }
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            setContextoApp(conn);
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM turmas WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
    }
}
