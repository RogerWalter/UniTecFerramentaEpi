package com.stizsoftware.unitecnicos.activity.model;

public class Movimentacao {
    private int id;
    private int id_tecnico;
    private String nome_tecnico;
    private String data;
    private String filial;
    private String tipo; // + ou -
    private int qtd_antiga;
    private int qtd_nova;
    private String motivo;
    private String imagem;
    private int id_grupo; // 1 - EPI | 0 - FERRAMENTA
    private int id_item;
    private String descricao_item;
    private String outros_item;
    private int id_tec_item; // esse campo é usado com o grupo, para sabermos onde foi registrado esta movimentação

    public Movimentacao() {
    }

    public Movimentacao(int id, int id_tecnico, String nome_tecnico, String data, String filial, String tipo, int qtd_antiga, int qtd_nova, String motivo, String imagem, int id_grupo, int id_item, String descricao_item, String marca_item, String outros_item, int id_tec_item) {
        this.id = id;
        this.id_tecnico = id_tecnico;
        this.nome_tecnico = nome_tecnico;
        this.data = data;
        this.filial = filial;
        this.tipo = tipo;
        this.qtd_antiga = qtd_antiga;
        this.qtd_nova = qtd_nova;
        this.motivo = motivo;
        this.imagem = imagem;
        this.id_grupo = id_grupo;
        this.id_item = id_item;
        this.descricao_item = descricao_item;
        this.outros_item = outros_item;
        this.id_tec_item = id_tec_item;
    }

    public String getNome_tecnico() {
        return nome_tecnico;
    }

    public void setNome_tecnico(String nome_tecnico) {
        this.nome_tecnico = nome_tecnico;
    }

    public String getDescricao_item() {
        return descricao_item;
    }

    public void setDescricao_item(String descricao_item) {
        this.descricao_item = descricao_item;
    }

    public String getOutros_item() {
        return outros_item;
    }

    public void setOutros_item(String outros_item) {
        this.outros_item = outros_item;
    }

    public int getId_tecnico() {
        return id_tecnico;
    }

    public void setId_tecnico(int id_tecnico) {
        this.id_tecnico = id_tecnico;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFilial() {
        return filial;
    }

    public void setFilial(String filial) {
        this.filial = filial;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getQtd_antiga() {
        return qtd_antiga;
    }

    public void setQtd_antiga(int qtd_antiga) {
        this.qtd_antiga = qtd_antiga;
    }

    public int getQtd_nova() {
        return qtd_nova;
    }

    public void setQtd_nova(int qtd_nova) {
        this.qtd_nova = qtd_nova;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public int getId_grupo() {
        return id_grupo;
    }

    public void setId_grupo(int id_grupo) {
        this.id_grupo = id_grupo;
    }

    public int getId_item() {
        return id_item;
    }

    public void setId_item(int id_item) {
        this.id_item = id_item;
    }

    public int getId_tec_item() {
        return id_tec_item;
    }

    public void setId_tec_item(int id_tec_item) {
        this.id_tec_item = id_tec_item;
    }
}


