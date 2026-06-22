package com.sge.model;

import java.sql.Timestamp;

public class Aluno {

    private int id;
    private String nome;
    private String matricula;
    private int turmaId;
    private String turmaNome; // somente para exibição
    private Timestamp createdAt;

    public Aluno() {}

    public Aluno(int id, String nome, String matricula, int turmaId) {
        this.id = id;
        this.nome = nome;
        this.matricula = matricula;
        this.turmaId = turmaId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public int getTurmaId() { return turmaId; }
    public void setTurmaId(int turmaId) { this.turmaId = turmaId; }

    public String getTurmaNome() { return turmaNome; }
    public void setTurmaNome(String turmaNome) { this.turmaNome = turmaNome; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return nome + " - " + matricula;
    }
}
