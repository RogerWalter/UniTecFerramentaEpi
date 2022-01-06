package com.stizsoftware.unitecnicos.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stizsoftware.unitecnicos.R;
import com.stizsoftware.unitecnicos.activity.model.RelatorioMovItem;
import com.stizsoftware.unitecnicos.activity.model.RelatorioTecItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdapterRelatorioMovItem extends RecyclerView.Adapter<AdapterRelatorioMovItem.MyViewHolder> {
    private List<RelatorioMovItem> listaMovItem;
    public AdapterRelatorioMovItem(List<RelatorioMovItem> lista) {
        this.listaMovItem = lista;
    }

    @NonNull
    @NotNull
    @Override
    public AdapterRelatorioMovItem.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_adapter_historico_movimentacao_tecnico, parent, false);
        return new AdapterRelatorioMovItem.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterRelatorioMovItem.MyViewHolder holder, int position) {
        RelatorioMovItem item = listaMovItem.get(position);

        holder.descricao.setText(item.getDescricao());
        holder.outros.setText(item.getOutros());
        holder.sentido.setText(item.getSentido());
        holder.data.setText(item.getData());

    }

    @Override
    public int getItemCount() {
        return listaMovItem.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView descricao;
        TextView outros;
        TextView sentido;
        TextView data;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            descricao = itemView.findViewById(R.id.textAdapterItem);
            outros = itemView.findViewById(R.id.textAdapterOutros);
            sentido = itemView.findViewById(R.id.textAdapterSentido);
            data = itemView.findViewById(R.id.textAdapterData);
        }
    }
    public void listaComFiltro(ArrayList<RelatorioMovItem> listaFiltrada)
    {
        listaMovItem = listaFiltrada;
        notifyDataSetChanged();
    }
}
