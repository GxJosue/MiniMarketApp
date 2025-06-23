package com.example.minimarketapp;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AgregarProductoActivity extends AppCompatActivity {

    EditText etNombre, etPrecio, etImagen;
    Button btnGuardar;

    private ProductoDao productoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        Toolbar toolbar = findViewById(R.id.toolbarAgregar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Agregar Productos");
        }
        toolbar.setTitleTextColor(Color.WHITE);
        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setTint(Color.WHITE);
        }

        etNombre = findViewById(R.id.etNombre);
        etPrecio = findViewById(R.id.etPrecio);
        etImagen = findViewById(R.id.etImagen);
        btnGuardar = findViewById(R.id.btnGuardarProducto);

        productoDao = AppDatabase.getInstance(getApplicationContext()).productoDao();

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String precioStr = etPrecio.getText().toString().trim();
            String imagen = etImagen.getText().toString().trim();

            if (TextUtils.isEmpty(nombre)) {
                etNombre.setError("Ingrese nombre");
                etNombre.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(precioStr)) {
                etPrecio.setError("Ingrese precio");
                etPrecio.requestFocus();
                return;
            }

            double precio;
            try {
                precio = Double.parseDouble(precioStr);
            } catch (NumberFormatException e) {
                etPrecio.setError("Precio inválido");
                etPrecio.requestFocus();
                return;
            }

            Producto nuevo = new Producto(nombre, precio, imagen);

            new Thread(() -> {
                productoDao.insertar(nuevo);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }).start();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
