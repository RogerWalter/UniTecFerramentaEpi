package com.stizsoftware.unitecnicos.activity.model;

public class TecnicoFerramenta {

    private int id;
    private int id_ferramenta;
    private String desc_ferramenta;
    private String marca_ferramenta;
    private String outros;
    private int id_tecnico;
    private  String nome_tecnico;
    private String imagem;
    private int qtd;
    private int id_movimentacao;
    private int status;

    public TecnicoFerramenta() {
    }

    public TecnicoFerramenta(int id, int id_ferramenta, String desc_ferramenta, String marca_ferramenta, String outros, int id_tecnico, String nome_tecnico, String imagem, int qtd, int id_movimentacao, int status) {
        this.id = id;
        this.id_ferramenta = id_ferramenta;
        this.desc_ferramenta = desc_ferramenta;
        this.marca_ferramenta = marca_ferramenta;
        this.outros = outros;
        this.id_tecnico = id_tecnico;
        this.nome_tecnico = nome_tecnico;
        this.imagem = imagem;
        this.qtd = qtd;
        this.id_movimentacao = id_movimentacao;
        this.status = status;
    }

    public String getMarca_ferramenta() {
        return marca_ferramenta;
    }

    public void setMarca_ferramenta(String marca_ferramenta) {
        this.marca_ferramenta = marca_ferramenta;
    }

    public int getId_movimentacao() {
        return id_movimentacao;
    }

    public void setId_movimentacao(int id_movimentacao) {
        this.id_movimentacao = id_movimentacao;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getOutros() {
        return outros;
    }

    public void setOutros(String outros) {
        this.outros = outros;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_ferramenta() {
        return id_ferramenta;
    }

    public void setId_ferramenta(int id_ferramenta) {
        this.id_ferramenta = id_ferramenta;
    }

    public String getDesc_ferramenta() {
        return desc_ferramenta;
    }

    public void setDesc_ferramenta(String desc_ferramenta) {
        this.desc_ferramenta = desc_ferramenta;
    }

    public int getId_tecnico() {
        return id_tecnico;
    }

    public void setId_tecnico(int id_tecnico) {
        this.id_tecnico = id_tecnico;
    }

    public String getNome_tecnico() {
        return nome_tecnico;
    }

    public void setNome_tecnico(String nome_tecnico) {
        this.nome_tecnico = nome_tecnico;
    }

    public int getQtd() {
        return qtd;
    }

    public void setQtd(int qtd) {
        this.qtd = qtd;
    }
}
