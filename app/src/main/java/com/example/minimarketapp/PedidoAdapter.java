package com.example.minimarketapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private ArrayList<Pedido> lista;
    private Context context;

    public PedidoAdapter(ArrayList<Pedido> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = lista.get(position);
        holder.tvCliente.setText("Cliente: " + pedido.cliente);
        holder.tvProducto.setText("Productos: " + pedido.productos);
        holder.tvEstado.setText("Estado: " + pedido.estado);
        holder.tvDireccion.setText("Dirección: " + pedido.direccion);


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String fechaFormato = sdf.format(new Date(pedido.fecha));
        holder.tvFecha.setText("Fecha: " + fechaFormato);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvCliente, tvDireccion, tvProducto, tvEstado, tvFecha;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvProducto = itemView.findViewById(R.id.tvProducto);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }
    }
}

