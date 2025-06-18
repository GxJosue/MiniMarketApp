package com.example.minimarketapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductosActivity extends AppCompatActivity {

    private RecyclerView rvProductos;
    private ProductoAdapter adapter;
    private ArrayList<Producto> listaProductos;
    private BDHelper dbHelper;
    private Button btnAgregarProducto;

    private static final int REQUEST_CODE_AGREGAR = 1; // Para recibir resultado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        rvProductos = findViewById(R.id.rvProductos);
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto);

        dbHelper = new BDHelper(this);
        dbHelper.insertarProductosPrueba();
        listaProductos = new ArrayList<>();

        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductoAdapter(listaProductos, this);
        rvProductos.setAdapter(adapter);

        cargarProductos();

        btnAgregarProducto.setOnClickListener(v -> {
            Intent intent = new Intent(ProductosActivity.this, AgregarProductoActivity.class);
            startActivityForResult(intent, REQUEST_CODE_AGREGAR);
        });
    }

    private void cargarProductos() {
        listaProductos.clear();
        Cursor cursor = dbHelper.obtenerProductos();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
                String imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen"));

                listaProductos.add(new Producto(id, nombre, precio, imagen));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_AGREGAR && resultCode == RESULT_OK) {
            cargarProductos(); // recarga la lista cuando vuelvas de agregar producto
        }
    }
}


