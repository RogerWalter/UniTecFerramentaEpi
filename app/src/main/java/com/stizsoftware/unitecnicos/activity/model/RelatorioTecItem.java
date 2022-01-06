package com.stizsoftware.unitecnicos.activity.model;

public class RelatorioTecItem {

    private String nomeTecnico;
    private String nomeItem;

    public RelatorioTecItem() {
    }

    public RelatorioTecItem(String nomeTecnico, String nomeFerramenta) {
        this.nomeTecnico = nomeTecnico;
        this.nomeItem = nomeFerramenta;
    }

    public String getNomeTecnico() {
        return nomeTecnico;
    }

    public void setNomeTecnico(String nomeTecnico) {
        this.nomeTecnico = nomeTecnico;
    }

    public String getNomeFerramenta() {
        return nomeItem;
    }

    public void setNomeItem(String nomeFerramenta) {
        this.nomeItem = nomeFerramenta;
    }
}
