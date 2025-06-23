package com.example.minimarketapp;

import android.content.Intent;
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
    private Button btnAgregarProducto;
    private ProductoDao productoDao;
    private AppDatabase appDatabase;


    private static final int REQUEST_CODE_AGREGAR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Productos");
        }
        toolbar.setTitleTextColor(Color.WHITE);
        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setTint(Color.WHITE);
        }

        rvProductos = findViewById(R.id.rvProductos);
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto);

        appDatabase = AppDatabase.getInstance(getApplicationContext());
        productoDao = appDatabase.productoDao();

        listaProductos = new ArrayList<>();
        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductoAdapter(listaProductos, this, productoDao, this::cargarProductos);
        rvProductos.setAdapter(adapter);

        insertarProductosSiNoExisten();
        cargarProductos();

        btnAgregarProducto.setOnClickListener(v -> {
            Intent intent = new Intent(ProductosActivity.this, AgregarProductoActivity.class);
            startActivityForResult(intent, REQUEST_CODE_AGREGAR);
        });
    }

    private void insertarProductosSiNoExisten() {
        new Thread(() -> {
            if (productoDao.obtenerTodos().isEmpty()) {
                productoDao.insertar(new Producto("Producto 1", 10.99, ""));
                productoDao.insertar(new Producto("Producto 2", 20.49, ""));
                productoDao.insertar(new Producto("Producto 3", 5.75, ""));
            }
        }).start();
    }

    private void cargarProductos() {
        new Thread(() -> {
            listaProductos.clear();
            listaProductos.addAll(productoDao.obtenerTodos());

            runOnUiThread(() -> adapter.notifyDataSetChanged());
        }).start();
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
        finish();
        return true;
    }
}
