package com.stizsoftware.unitecnicos.activity.model;

public class Totalizador {
    private int idItem;
    private String descricao;
    private String outros;
    private int qtd;

    public Totalizador() {
    }

    public Totalizador(int idItem, String descricao, String outros, int qtd) {
        this.idItem = idItem;
        this.descricao = descricao;
        this.outros = outros;
        this.qtd = qtd;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getOutros() {
        return outros;
    }

    public void setOutros(String outros) {
        this.outros = outros;
    }

    public int getQtd() {
        return qtd;
    }

    public void setQtd(int qtd) {
        this.qtd = qtd;
    }
}
