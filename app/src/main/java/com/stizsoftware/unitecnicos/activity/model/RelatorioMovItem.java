package com.stizsoftware.unitecnicos.activity.model;

public class RelatorioMovItem {

    private String descricao;
    private String outros;
    private String sentido;
    private String data;
    private String imagem_firebase;

    public RelatorioMovItem() {
    }

    public RelatorioMovItem(String descricao, String outros, String sentido, String data, String imagem_firebase) {
        this.descricao = descricao;
        this.outros = outros;
        this.sentido = sentido;
        this.data = data;
        this.imagem_firebase = imagem_firebase;
    }

    public String getImagem_firebase() {
        return imagem_firebase;
    }

    public void setImagem_firebase(String imagem_firebase) {
        this.imagem_firebase = imagem_firebase;
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

    public String getSentido() {
        return sentido;
    }

    public void setSentido(String sentido) {
        this.sentido = sentido;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
