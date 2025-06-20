package com.example.minimarketapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.graphics.Color;

import java.util.ArrayList;

public class ProductosActivity extends AppCompatActivity {

    private RecyclerView rvProductos;
    private ProductoAdapter adapter;
    private ArrayList<Producto> listaProductos;
    private BDHelper dbHelper;
    private Button btnAgregarProducto;

    private static final int REQUEST_CODE_AGREGAR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Muestra botón atrás
            getSupportActionBar().setTitle("Productos");
        }
        toolbar.setTitleTextColor(Color.WHITE);
        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setTint(Color.WHITE);  // Flecha blanca
        }

        rvProductos = findViewById(R.id.rvProductos);
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto);

        dbHelper = new BDHelper(this);
        dbHelper.insertarProductosPrueba(); // Solo si aún no hay datos
        listaProductos = new ArrayList<>();

        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductoAdapter(listaProductos, this, dbHelper, this::cargarProductos);
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
        if (requestCode == REQUEST_CODE_AGREGAR && resultCode == RESULT_OK) {
            cargarProductos();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // cierra esta actividad y vuelve a la anterior
        return true;
    }
}
