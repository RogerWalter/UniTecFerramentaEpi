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

public class AdapterTecnicoFerramenta extends RecyclerView.Adapter<AdapterTecnicoFerramenta.MyViewHolder> {

    private List<TecnicoFerramenta> listaTecnicoFerramenta;
    public AdapterTecnicoFerramenta(List<TecnicoFerramenta> lista) {this.listaTecnicoFerramenta = lista;}

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_adapter_detalhes_lista_ferramenta, parent, false);

        return new AdapterTecnicoFerramenta.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        TecnicoFerramenta tecnicoFerramenta = listaTecnicoFerramenta.get(position);

        holder.descricao.setText(tecnicoFerramenta.getDesc_ferramenta());
        holder.qtd.setText(String.valueOf(tecnicoFerramenta.getQtd()));

    }

    @Override
    public int getItemCount() {
        return listaTecnicoFerramenta.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView descricao;
        TextView qtd;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            descricao = itemView.findViewById(R.id.textDescricaoAdapterFerramentaDetalhes);
            qtd = itemView.findViewById(R.id.textQtdAdapterFerramentaDetalhes);
        }
    }

    public void listaComFiltro(ArrayList<TecnicoFerramenta> listaTecnicoFerramentaFiltrada)
    {
        listaTecnicoFerramenta = listaTecnicoFerramentaFiltrada;
        notifyDataSetChanged();
    }

}
