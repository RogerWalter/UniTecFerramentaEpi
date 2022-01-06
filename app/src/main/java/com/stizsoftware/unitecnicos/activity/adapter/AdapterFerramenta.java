package com.stizsoftware.unitecnicos.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stizsoftware.unitecnicos.R;
import com.stizsoftware.unitecnicos.activity.model.Ferramenta;

import java.util.ArrayList;
import java.util.List;

public class AdapterFerramenta extends RecyclerView.Adapter<AdapterFerramenta.MyViewHolder> {

    private List<Ferramenta> listaFerramentas;
    public AdapterFerramenta(List<Ferramenta> lista) {
        this.listaFerramentas = lista;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_adapter_lista_ferramenta, parent, false);

        return new AdapterFerramenta.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Ferramenta ferramenta = listaFerramentas.get(position);

        //holder.id.setText(String.valueOf(ferramenta.getId()));
        holder.descricao.setText(ferramenta.getDescricao());

    }

    @Override
    public int getItemCount() {
        return listaFerramentas.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        //TextView id;
        TextView descricao;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //id = itemView.findViewById(R.id.textIdAdapterFerramenta);
            descricao = itemView.findViewById(R.id.textDescricaoAdapterFerramenta);
        }
    }

    public void listaComFiltro(ArrayList<Ferramenta> listaFiltrada)
    {
        listaFerramentas = listaFiltrada;
        notifyDataSetChanged();
    }

}
