package com.stizsoftware.unitecnicos.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stizsoftware.unitecnicos.R;
import com.stizsoftware.unitecnicos.activity.model.RelatorioTecItem;
import com.stizsoftware.unitecnicos.activity.model.Totalizador;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdapterRelatorioTotalizacao extends RecyclerView.Adapter<AdapterRelatorioTotalizacao.MyViewHolder>{
    private List<Totalizador> listaTotais;
    public AdapterRelatorioTotalizacao(List<Totalizador> lista) {
        this.listaTotais = lista;
    }


    @NonNull
    @NotNull
    @Override
    public AdapterRelatorioTotalizacao.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_adapter_totalizador, parent, false);

        return new AdapterRelatorioTotalizacao.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterRelatorioTotalizacao.MyViewHolder holder, int position) {
        Totalizador total = listaTotais.get(position);

        holder.descricao.setText(total.getDescricao());
        holder.outros.setText(total.getOutros());
        holder.qtd.setText(String.valueOf(total.getQtd()));
    }

    @Override
    public int getItemCount() {
        return listaTotais.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView descricao;
        TextView outros;
        TextView qtd;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            descricao = itemView.findViewById(R.id.textDescricaoAdapterTotal);
            outros = itemView.findViewById(R.id.textOutrosAdapterTotal);
            qtd = itemView.findViewById(R.id.textQtdAdapterTotal);
        }
    }
    public void listaComFiltro(ArrayList<Totalizador> listaFiltrada)
    {
        listaTotais = listaFiltrada;
        notifyDataSetChanged();
    }
}
