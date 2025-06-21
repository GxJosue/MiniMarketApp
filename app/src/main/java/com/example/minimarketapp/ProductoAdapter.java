package com.example.minimarketapp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private ArrayList<Producto> listaProductos;
    private Context context;
    private BDHelper dbHelper;
    private OnProductoEliminadoListener listener;

    // Interfaz para notificar a la actividad que se eliminó un producto
    public interface OnProductoEliminadoListener {
        void onProductoEliminado();
    }

    public ProductoAdapter(ArrayList<Producto> listaProductos, Context context, BDHelper dbHelper, OnProductoEliminadoListener listener) {
        this.listaProductos = listaProductos;
        this.context = context;
        this.dbHelper = dbHelper;
        this.listener = listener;
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

        // Evento al pulsar el botón eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar producto")
                    .setMessage("¿Estás seguro de que deseas eliminar este producto?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        dbHelper.eliminarProducto(producto.getId());
                        listaProductos.remove(position);
                        notifyItemRemoved(position);
                        listener.onProductoEliminado();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPrecio;
        ImageView imgProducto;
        ImageButton btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvPrecio = itemView.findViewById(R.id.tvPrecioProducto);
            imgProducto = itemView.findViewById(R.id.imgProducto);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
