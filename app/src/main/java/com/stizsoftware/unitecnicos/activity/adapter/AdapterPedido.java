package com.stizsoftware.unitecnicos.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stizsoftware.unitecnicos.R;
import com.stizsoftware.unitecnicos.activity.model.Pedido;
import com.stizsoftware.unitecnicos.activity.model.Totalizador;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdapterPedido extends RecyclerView.Adapter<AdapterPedido.MyViewHolder> {
    private List<Pedido> listaPedido;
    public AdapterPedido(List<Pedido> lista) {
        this.listaPedido = lista;
    }


    @NonNull
    @NotNull
    @Override
    public AdapterPedido.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_adapter_lista_item_pedido, parent, false);

        return new AdapterPedido.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterPedido.MyViewHolder holder, int position) {
        Pedido pedido = listaPedido.get(position);

        holder.descricao.setText(pedido.getDescricao());
        holder.qtd.setText(pedido.getQtd());
        holder.tecnico.setText(String.valueOf(pedido.getTecnico()));
    }

    @Override
    public int getItemCount() {
        return listaPedido.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView descricao;
        TextView qtd;
        TextView tecnico;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            descricao = itemView.findViewById(R.id.textDescricaoAdapterPedido);
            qtd = itemView.findViewById(R.id.textQtdPedido);
            tecnico = itemView.findViewById(R.id.textTecnicoPedido);
        }
    }
    public void listaComFiltro(ArrayList<Pedido> listaFiltrada)
    {
        listaPedido = listaFiltrada;
        notifyDataSetChanged();
    }
}
