package com.example.minimarketapp;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.*;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class PedidosActivity extends AppCompatActivity {

    private RecyclerView rvPedidos;
    private PedidoAdapter adapter;
    private ArrayList<Pedido> listaPedidos;
    private Button btnAgregarPedido;

    private PedidoDao pedidoDao;
    private ProductoDao productoDao;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        Toolbar toolbar = findViewById(R.id.toolbarPedidos);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // mostrar flecha atrás
            getSupportActionBar().setTitle("Pedidos");
        }

        toolbar.setTitleTextColor(Color.WHITE);
        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setTint(Color.WHITE);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvPedidos = findViewById(R.id.rvPedidos);
        btnAgregarPedido = findViewById(R.id.btnAgregarPedido);

        appDatabase = AppDatabase.getInstance(getApplicationContext());
        pedidoDao = appDatabase.pedidoDao();
        productoDao = appDatabase.productoDao();

        listaPedidos = new ArrayList<>();
        adapter = new PedidoAdapter(listaPedidos, this);
        rvPedidos.setLayoutManager(new LinearLayoutManager(this));
        rvPedidos.setAdapter(adapter);

        cargarPedidos();

        btnAgregarPedido.setOnClickListener(v -> mostrarDialogoAgregar());
    }

    private void cargarPedidos() {
        new Thread(() -> {
            List<Pedido> pedidos = pedidoDao.obtenerTodos();
            runOnUiThread(() -> {
                listaPedidos.clear();
                listaPedidos.addAll(pedidos);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void mostrarDialogoAgregar() {
        View vista = LayoutInflater.from(this).inflate(R.layout.dialogo_agregar_pedido, null);
        EditText edtCliente = vista.findViewById(R.id.edtCliente);
        EditText edtDireccion = vista.findViewById(R.id.edtDireccion);
        ListView listProductos = vista.findViewById(R.id.listProductos);

        new Thread(() -> {
            List<Producto> productos = productoDao.obtenerTodos();
            ArrayList<String> nombresProductos = new ArrayList<>();
            for (Producto p : productos) {
                nombresProductos.add(p.getNombre());
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> adapterProductos = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, nombresProductos);
                listProductos.setAdapter(adapterProductos);
                listProductos.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Agregar Pedido");
                builder.setView(vista);
                builder.setPositiveButton("Guardar", (dialog, which) -> {
                    String cliente = edtCliente.getText().toString();
                    String direccion = edtDireccion.getText().toString();
                    long fecha = System.currentTimeMillis();

                    StringBuilder productosSeleccionados = new StringBuilder();
                    for (int i = 0; i < listProductos.getCount(); i++) {
                        if (listProductos.isItemChecked(i)) {
                            productosSeleccionados.append(nombresProductos.get(i)).append(", ");
                        }
                    }

                    if (productosSeleccionados.length() > 0) {
                        productosSeleccionados.setLength(productosSeleccionados.length() - 2);
                    }

                    Pedido pedido = new Pedido(cliente, direccion, productosSeleccionados.toString(), "Pendiente", fecha);
                    new Thread(() -> {
                        pedidoDao.insertar(pedido);
                        runOnUiThread(this::cargarPedidos);
                    }).start();
                });
                builder.setNegativeButton("Cancelar", null);
                builder.show();
            });
        }).start();
    }
}
