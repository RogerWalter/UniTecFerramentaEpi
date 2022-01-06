package com.stizsoftware.unitecnicos.activity.model;

public class TecnicoEpi {

    private int id;
    private int id_epi;
    private String desc_epi;
    private String outros;
    private int id_tecnico;
    private String nome_tecnico;
    private String imagem;
    private int qtd;
    private int id_movimentacao;
    private int status;

    public TecnicoEpi() {
    }

    public TecnicoEpi(int id, int id_epi, String desc_epi, String outros, int id_tecnico, String nome_tecnico, String imagem, int qtd, int id_movimentacao, int status) {
        this.id = id;
        this.id_epi = id_epi;
        this.desc_epi = desc_epi;
        this.outros = outros;
        this.id_tecnico = id_tecnico;
        this.nome_tecnico = nome_tecnico;
        this.imagem = imagem;
        this.qtd = qtd;
        this.id_movimentacao = id_movimentacao;
        this.status = status;
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

    public int getId_epi() {
        return id_epi;
    }

    public void setId_epi(int id_epi) {
        this.id_epi = id_epi;
    }

    public String getDesc_epi() {
        return desc_epi;
    }

    public void setDesc_epi(String desc_epi) {
        this.desc_epi = desc_epi;
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
