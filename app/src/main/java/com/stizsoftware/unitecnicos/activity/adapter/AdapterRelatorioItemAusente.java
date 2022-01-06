package com.stizsoftware.unitecnicos.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stizsoftware.unitecnicos.R;
import com.stizsoftware.unitecnicos.activity.model.Epi;
import com.stizsoftware.unitecnicos.activity.model.RelatorioTecItem;
import com.stizsoftware.unitecnicos.activity.model.Totalizador;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdapterRelatorioItemAusente extends RecyclerView.Adapter<AdapterRelatorioItemAusente.MyViewHolder>{
    private List<RelatorioTecItem> listaTecItensAusentes;
    public AdapterRelatorioItemAusente(List<RelatorioTecItem> lista) {
        this.listaTecItensAusentes = lista;
    }

    @NonNull
    @NotNull
    @Override
    public AdapterRelatorioItemAusente.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_adapter_relatorio_item_ausente, parent, false);
        return new AdapterRelatorioItemAusente.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterRelatorioItemAusente.MyViewHolder holder, int position) {
        RelatorioTecItem item = listaTecItensAusentes.get(position);

        holder.tecnico.setText(item.getNomeTecnico());
        holder.item.setText(item.getNomeFerramenta());
    }

    @Override
    public int getItemCount() {
        return listaTecItensAusentes.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView tecnico;
        TextView item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tecnico = itemView.findViewById(R.id.textAdapterTecItemTecnico);
            item = itemView.findViewById(R.id.textAdapterTecItemItem);
        }
    }
    public void listaComFiltro(ArrayList<RelatorioTecItem> listaFiltrada)
    {
        listaTecItensAusentes = listaFiltrada;
        notifyDataSetChanged();
    }
}
