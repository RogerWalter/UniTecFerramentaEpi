package com.stizsoftware.unitecnicos.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stizsoftware.unitecnicos.R;
import com.stizsoftware.unitecnicos.activity.model.Ferramenta;
import com.stizsoftware.unitecnicos.activity.model.Tecnico;

import java.util.ArrayList;
import java.util.List;

public class AdapterTecnico extends RecyclerView.Adapter<AdapterTecnico.MyViewHolder> {

    private List<Tecnico> listaTecnicos;
    public AdapterTecnico(List<Tecnico> lista) {
        this.listaTecnicos = lista;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_adapter_lista_tecnico, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Tecnico tecnico = listaTecnicos.get(position);

        //holder.ic.setImageDrawable(R.id.icAdapterTecnico);
        holder.nome.setText(tecnico.getNome());

    }

    @Override
    public int getItemCount() {
        return listaTecnicos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        ImageView ic, ic1;
        TextView nome;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ic = itemView.findViewById(R.id.icAdapterTecnico);
            nome = itemView.findViewById(R.id.textNomeAdapterTecnico);
            ic1 = itemView.findViewById(R.id.icDireitaAdapterTecnico);

        }
    }
    public void listaComFiltro(ArrayList<Tecnico> listaFiltrada)
    {
        listaTecnicos = listaFiltrada;
        notifyDataSetChanged();
    }

    /*
    @NonNull
    @Override
    public AdapterTecnico.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_adapter_lista_tecnico, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Tecnico tecnico = listaTecnicos.get(position);

        holder.id.setText(tecnico.getId());
        holder.nome.setText(tecnico.getNome());
    }

    @Override
    public int getItemCount() {
        return listaTecnicos.size();
    }
     */
}
