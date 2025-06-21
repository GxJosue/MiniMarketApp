package com.example.minimarketapp;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        setContentView(R.layout.activity_pedidos);
        rvPedidos = findViewById(R.id.rvPedidos);
        btnAgregarPedido = findViewById(R.id.btnAgregarPedido);
        dbHelper = new BDHelper(this);
        dbHelper.insertarProductosPrueba();
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
            String direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion"));
            String productos = cursor.getString(cursor.getColumnIndexOrThrow("productos"));
            String estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"));
            long fecha = cursor.getLong(cursor.getColumnIndexOrThrow("fecha"));

            listaPedidos.add(new Pedido(id, cliente, direccion, productos, estado, fecha));
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }


    private void mostrarDialogoAgregar() {
        View vista = LayoutInflater.from(this).inflate(R.layout.dialogo_agregar_pedido, null);
        EditText edtCliente = vista.findViewById(R.id.edtCliente);
        EditText edtDireccion = vista.findViewById(R.id.edtDireccion);
        ListView listProductos = vista.findViewById(R.id.listProductos);

        ArrayList<String> nombresProductos = new ArrayList<>();
        Cursor cursor = dbHelper.obtenerProductos();
        while (cursor.moveToNext()) {
            nombresProductos.add(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
        }
        cursor.close();

        ArrayAdapter<String> adapterProductos = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, nombresProductos);
        listProductos.setAdapter(adapterProductos);

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

            dbHelper.agregarPedido(cliente, direccion, productosSeleccionados.toString(), "Pendiente", fecha);
            cargarPedidos();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

}

