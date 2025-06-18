package com.example.minimarketapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private ArrayList<Producto> listaProductos;
    private Context context;

    public ProductoAdapter(ArrayList<Producto> listaProductos, Context context) {
        this.listaProductos = listaProductos;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoAdapter.ViewHolder holder, int position) {
        Producto producto = listaProductos.get(position);
        holder.tvNombre.setText(producto.getNombre());
        holder.tvPrecio.setText(String.format("$ %.2f", producto.getPrecio()));
        // Para imagen, por ahora usamos la de ejemplo en item_producto.xml
        // Luego puedes cargar imagen con Glide o Picasso si quieres
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPrecio;
        ImageView imgProducto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvPrecio = itemView.findViewById(R.id.tvPrecioProducto);
            imgProducto = itemView.findViewById(R.id.imgProducto);
        }
    }
}
