package com.stizsoftware.unitecnicos.activity.model;

public class Epi {

    private int id;
    private String descricao;
    private int status;
    private String outros;


    public Epi() {
    }

    public Epi(int id, String descricao, int status, String outros) {
        this.id = id;
        this.descricao = descricao;
        this.status = status;
        this.outros = outros;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOutros() {
        return outros;
    }

    public void setOutros(String outros) {
        this.outros = outros;
    }
}
