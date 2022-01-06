package com.stizsoftware.unitecnicos.activity.model;

public class Tecnico {

    private int id;
    private String nome;
    private String filial;
    private int status;

    public Tecnico(int id, String nome, String filial, int status) {
        this.id = id;
        this.nome = nome;
        this.filial = filial;
        this.status = status;
    }

    public Tecnico() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFilial() {
        return filial;
    }

    public void setFilial(String filial) {
        this.filial = filial;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
