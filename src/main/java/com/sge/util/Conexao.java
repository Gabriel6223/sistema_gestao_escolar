package com.sge.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitária para gerenciar a conexão JDBC com o MySQL.
 * Configure URL, usuário e senha do banco conforme seu ambiente.
 */
public class Conexao {

    private static final String URL =
        System.getenv().getOrDefault("SGE_DB_URL",
            "jdbc:mysql://localhost:3306/sge?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=utf8");
    private static final String USER =
        System.getenv().getOrDefault("SGE_DB_USER", "root");
    private static final String PASSWORD =
        System.getenv().getOrDefault("SGE_DB_PASSWORD", ""); // ajuste para sua senha do MySQL

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do MySQL não encontrado.", e);
        }
    }

    /**
     * Testa a conexão. Útil para diagnóstico inicial.
     */
    public static boolean testar() {
        try (Connection c = getConnection()) {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
