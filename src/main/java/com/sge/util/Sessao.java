package com.sge.util;

import com.sge.model.Usuario;

/**
 * Mantém o usuário autenticado em memória durante a sessão do sistema.
 * Implementação simples (single-user) para o padrão desktop Swing.
 */
public class Sessao {

    private static Usuario usuarioLogado;

    private Sessao() {}

    public static void setUsuario(Usuario u) {
        usuarioLogado = u;
    }

    public static Usuario getUsuario() {
        return usuarioLogado;
    }

    public static boolean isLogado() {
        return usuarioLogado != null;
    }

    public static void logout() {
        usuarioLogado = null;
    }
}
