package com.sge.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Nota {

    private int id;
    private int alunoId;
    private String alunoNome;       // somente para exibição
    private String disciplina;
    private BigDecimal valor;
    private int bimestre;
    private Timestamp createdAt;

    public Nota() {}

    public Nota(int id, int alunoId, String disciplina, BigDecimal valor, int bimestre) {
        this.id = id;
        this.alunoId = alunoId;
        this.disciplina = disciplina;
        this.valor = valor;
        this.bimestre = bimestre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAlunoId() { return alunoId; }
    public void setAlunoId(int alunoId) { this.alunoId = alunoId; }

    public String getAlunoNome() { return alunoNome; }
    public void setAlunoNome(String alunoNome) { this.alunoNome = alunoNome; }

    public String getDisciplina() { return disciplina; }
    public void setDisciplina(String disciplina) { this.disciplina = disciplina; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public int getBimestre() { return bimestre; }
    public void setBimestre(int bimestre) { this.bimestre = bimestre; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
