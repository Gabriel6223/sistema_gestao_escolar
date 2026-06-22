package com.sge.model;

import java.sql.Timestamp;

public class Usuario {

    private int id;
    private String login;
    private String senha;
    private String nome;
    private String perfil;
    private boolean ativo;
    private Timestamp createdAt;

    public Usuario() {}

    public Usuario(int id, String login, String senha, String nome, String perfil, boolean ativo) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.perfil = perfil;
        this.ativo = ativo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return nome + " (" + login + ")";
    }
}
