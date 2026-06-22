package com.sge.dao;

import com.sge.model.Usuario;
import com.sge.util.Conexao;
import com.sge.util.HashUtil;
import com.sge.util.Sessao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private void setContextoApp(Connection conn) throws SQLException {
        // Alimenta as variáveis de sessão consumidas pelas triggers de log
        if (Sessao.isLogado()) {
            try (PreparedStatement ps = conn.prepareStatement("SET @app_user_id = ?, @app_user_login = ?")) {
                ps.setInt(1, Sessao.getUsuario().getId());
                ps.setString(2, Sessao.getUsuario().getLogin());
                ps.execute();
            }
        } else {
            try (PreparedStatement ps = conn.prepareStatement("SET @app_user_id = NULL, @app_user_login = 'sistema'")) {
                ps.execute();
            }
        }
    }

    public Usuario autenticar(String login, String senha) throws SQLException {
        String sql = "SELECT id, login, senha, nome, perfil, ativo FROM usuarios WHERE login = ? AND ativo = 1";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashBanco = rs.getString("senha");
                    if (hashBanco.equals(HashUtil.sha256(senha))) {
                        Usuario u = new Usuario();
                        u.setId(rs.getInt("id"));
                        u.setLogin(rs.getString("login"));
                        u.setNome(rs.getString("nome"));
                        u.setPerfil(rs.getString("perfil"));
                        u.setAtivo(rs.getBoolean("ativo"));
                        return u;
                    }
                }
            }
        }
        return null;
    }

    public List<Usuario> listar() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, login, nome, perfil, ativo, created_at FROM usuarios ORDER BY nome";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setLogin(rs.getString("login"));
                u.setNome(rs.getString("nome"));
                u.setPerfil(rs.getString("perfil"));
                u.setAtivo(rs.getBoolean("ativo"));
                u.setCreatedAt(rs.getTimestamp("created_at"));
                lista.add(u);
            }
        }
        return lista;
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, login, nome, perfil, ativo, created_at FROM usuarios WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setLogin(rs.getString("login"));
                    u.setNome(rs.getString("nome"));
                    u.setPerfil(rs.getString("perfil"));
                    u.setAtivo(rs.getBoolean("ativo"));
                    u.setCreatedAt(rs.getTimestamp("created_at"));
                    return u;
                }
            }
        }
        return null;
    }

    public void inserir(Usuario u) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            setContextoApp(conn);
            String sql = "INSERT INTO usuarios (login, senha, nome, perfil, ativo) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, u.getLogin());
                ps.setString(2, HashUtil.sha256(u.getSenha()));
                ps.setString(3, u.getNome());
                ps.setString(4, u.getPerfil() == null ? "ADMIN" : u.getPerfil());
                ps.setBoolean(5, u.isAtivo());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) u.setId(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Usuario u) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            setContextoApp(conn);
            StringBuilder sql = new StringBuilder("UPDATE usuarios SET nome = ?, perfil = ?, ativo = ?");
            boolean alteraSenha = u.getSenha() != null && !u.getSenha().isBlank();
            if (alteraSenha) sql.append(", senha = ?");
            sql.append(" WHERE id = ?");
            try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                ps.setString(1, u.getNome());
                ps.setString(2, u.getPerfil() == null ? "ADMIN" : u.getPerfil());
                ps.setBoolean(3, u.isAtivo());
                if (alteraSenha) ps.setString(4, HashUtil.sha256(u.getSenha()));
                ps.setInt(alteraSenha ? 5 : 4, u.getId());
                ps.executeUpdate();
            }
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            setContextoApp(conn);
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM usuarios WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
    }
}
