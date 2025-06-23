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
    private ProductoDao productoDao;
    private OnProductoEliminadoListener listener;

    public interface OnProductoEliminadoListener {
        void onProductoEliminado();
    }

    public ProductoAdapter(ArrayList<Producto> listaProductos, Context context, ProductoDao productoDao, OnProductoEliminadoListener listener) {
        this.listaProductos = listaProductos;
        this.context = context;
        this.productoDao = productoDao;
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

        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar producto")
                    .setMessage("¿Estás seguro de que deseas eliminar este producto?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        new Thread(() -> {
                            productoDao.eliminarProducto(producto);  // Room se encarga del ID
                            ((ProductosActivity) context).runOnUiThread(() -> {
                                listaProductos.remove(position);
                                notifyItemRemoved(position);
                                listener.onProductoEliminado();
                            });
                        }).start();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
