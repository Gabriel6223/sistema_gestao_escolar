package com.sge.model;

import java.sql.Timestamp;

public class Turma {

    private int id;
    private String nome;
    private String descricao;
    private int ano;
    private Timestamp createdAt;

    public Turma() {}

    public Turma(int id, String nome, String descricao, int ano) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.ano = ano;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return nome + " (" + ano + ")";
    }
}
