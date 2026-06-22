package com.sge.dao;

import com.sge.model.Log;
import com.sge.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LogDAO {

    public List<Log> listar(int limite) throws SQLException {
        List<Log> lista = new ArrayList<>();
        String sql = "SELECT id, usuario_id, usuario_login, acao, tabela, registro_id, detalhes, created_at " +
                     "FROM logs ORDER BY created_at DESC LIMIT ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limite <= 0 ? 200 : limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Log l = new Log();
                    l.setId(rs.getInt("id"));
                    int uid = rs.getInt("usuario_id");
                    l.setUsuarioId(rs.wasNull() ? null : uid);
                    l.setUsuarioLogin(rs.getString("usuario_login"));
                    l.setAcao(rs.getString("acao"));
                    l.setTabela(rs.getString("tabela"));
                    int rid = rs.getInt("registro_id");
                    l.setRegistroId(rs.wasNull() ? null : rid);
                    l.setDetalhes(rs.getString("detalhes"));
                    l.setCreatedAt(rs.getTimestamp("created_at"));
                    lista.add(l);
                }
            }
        }
        return lista;
    }

    /** Insere manualmente (login/logout e logs vindos da própria aplicação). */
    public void registrar(Integer usuarioId, String usuarioLogin,
                          String acao, String tabela, Integer registroId, String detalhes) throws SQLException {
        String sql = "INSERT INTO logs (usuario_id, usuario_login, acao, tabela, registro_id, detalhes) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (usuarioId == null) ps.setNull(1, java.sql.Types.INTEGER);
            else ps.setInt(1, usuarioId);
            ps.setString(2, usuarioLogin);
            ps.setString(3, acao);
            ps.setString(4, tabela);
            if (registroId == null) ps.setNull(5, java.sql.Types.INTEGER);
            else ps.setInt(5, registroId);
            ps.setString(6, detalhes);
            ps.executeUpdate();
        }
    }
}
