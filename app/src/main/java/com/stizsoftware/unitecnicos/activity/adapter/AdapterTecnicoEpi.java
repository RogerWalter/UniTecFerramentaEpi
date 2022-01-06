package com.stizsoftware.unitecnicos.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stizsoftware.unitecnicos.R;
import com.stizsoftware.unitecnicos.activity.model.Ferramenta;
import com.stizsoftware.unitecnicos.activity.model.TecnicoEpi;
import com.stizsoftware.unitecnicos.activity.model.TecnicoFerramenta;

import java.util.ArrayList;
import java.util.List;

public class AdapterTecnicoEpi extends RecyclerView.Adapter<AdapterTecnicoEpi.MyViewHolder> {

    private List<TecnicoEpi> listaTecnicoEpi;
    public AdapterTecnicoEpi(List<TecnicoEpi> lista) {
        this.listaTecnicoEpi = lista;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_adapter_detalhes_lista_epi, parent, false);

        return new AdapterTecnicoEpi.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        TecnicoEpi tecnicoEpi = listaTecnicoEpi.get(position);

        holder.descricao.setText(tecnicoEpi.getDesc_epi());
        holder.qtd.setText(String.valueOf(tecnicoEpi.getQtd()));

    }

    @Override
    public int getItemCount() {
        return listaTecnicoEpi.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView descricao;
        TextView qtd;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            descricao = itemView.findViewById(R.id.textDescricaAdapterEpiDetalhes);
            qtd = itemView.findViewById(R.id.textQtdAdapterEpiDetalhes);
        }
    }

    public void listaComFiltro(ArrayList<TecnicoEpi> listaTecnicoEpiFiltrada)
    {
        listaTecnicoEpi = listaTecnicoEpiFiltrada;
        notifyDataSetChanged();
    }

}
