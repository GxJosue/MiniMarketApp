package com.example.minimarketapp;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PedidosActivity extends AppCompatActivity {

    private RecyclerView rvPedidos;
    private PedidoAdapter adapter;
    private ArrayList<Pedido> listaPedidos;
    private BDHelper dbHelper;
    private Button btnAgregarPedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("PedidosActivity", "onCreate started");
        super.onCreate(savedInstanceState);
        dbHelper.insertarProductosPrueba();
        Log.d("PedidosActivity", "layout set");
        setContentView(R.layout.activity_pedidos);
        Log.d("PedidosActivity", "onCreate started");
        rvPedidos = findViewById(R.id.rvPedidos);
        btnAgregarPedido = findViewById(R.id.btnAgregarPedido);
        dbHelper = new BDHelper(this);
        listaPedidos = new ArrayList<>();

        rvPedidos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PedidoAdapter(listaPedidos, this);
        rvPedidos.setAdapter(adapter);

        cargarPedidos();

        btnAgregarPedido.setOnClickListener(v -> mostrarDialogoAgregar());
    }

    private void cargarPedidos() {
        listaPedidos.clear();
        Cursor cursor = dbHelper.obtenerPedidos();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String cliente = cursor.getString(cursor.getColumnIndexOrThrow("cliente"));
            String producto = cursor.getString(cursor.getColumnIndexOrThrow("producto"));
            String estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"));
            long fecha = cursor.getLong(cursor.getColumnIndexOrThrow("fecha"));

            listaPedidos.add(new Pedido(id, cliente, producto, estado, fecha));
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void mostrarDialogoAgregar() {
        View vista = LayoutInflater.from(this).inflate(R.layout.dialogo_agregar_pedido, null);
        EditText edtCliente = vista.findViewById(R.id.edtCliente);
        Spinner spnProductos = vista.findViewById(R.id.spnProductos);

        ArrayList<String> nombres = new ArrayList<>();
        Cursor cursor = dbHelper.obtenerProductos();
        while (cursor.moveToNext()) {
            nombres.add(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
        }
        cursor.close();

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnProductos.setAdapter(spinnerAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar Pedido");
        builder.setView(vista);
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String cliente = edtCliente.getText().toString();
            String producto = spnProductos.getSelectedItem().toString();
            long fecha = System.currentTimeMillis();

            dbHelper.agregarPedido(cliente, producto, "Pendiente", fecha);
            cargarPedidos();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
}

