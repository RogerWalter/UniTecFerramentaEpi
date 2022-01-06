package com.stizsoftware.unitecnicos.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stizsoftware.unitecnicos.R;
import com.stizsoftware.unitecnicos.activity.model.Epi;
import com.stizsoftware.unitecnicos.activity.model.Ferramenta;

import java.util.ArrayList;
import java.util.List;

public class AdapterEpi extends RecyclerView.Adapter<AdapterEpi.MyViewHolder> {

    private List<Epi> listaEpi;
    public AdapterEpi(List<Epi> lista) {
        this.listaEpi = lista;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_adapter_lista_epi, parent, false);

        return new AdapterEpi.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Epi epi = listaEpi.get(position);

        //holder.id.setText(String.valueOf(epi.getId()));
        holder.descricao.setText(epi.getDescricao());

    }

    @Override
    public int getItemCount() {
        return listaEpi.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        //TextView id;
        TextView descricao;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //id = itemView.findViewById(R.id.textIdAdapterEpi);
            descricao = itemView.findViewById(R.id.textDescricaAdapterEpi);
        }
    }
    public void listaComFiltro(ArrayList<Epi> listaFiltrada)
    {
        listaEpi = listaFiltrada;
        notifyDataSetChanged();
    }
}
