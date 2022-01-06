package com.stizsoftware.unitecnicos.activity.model;

public class Pedido {
    private int id;
    private String descricao;
    private String qtd;
    private String tecnico;

    public Pedido() {
    }

    public Pedido(int id, String descricao, String qtd, String tecnico) {
        this.id = id;
        this.descricao = descricao;
        this.qtd = qtd;
        this.tecnico = tecnico;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTecnico() {
        return tecnico;
    }

    public void setTecnico(String tecnico) {
        this.tecnico = tecnico;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getQtd() {
        return qtd;
    }

    public void setQtd(String qtd) {
        this.qtd = qtd;
    }
}
