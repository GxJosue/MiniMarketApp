package com.example.minimarketapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AgregarProductoActivity extends AppCompatActivity {

    EditText etNombre, etPrecio, etImagen;
    Button btnGuardar;
    BDHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        etNombre = findViewById(R.id.etNombre);
        etPrecio = findViewById(R.id.etPrecio);
        etImagen = findViewById(R.id.etImagen);
        btnGuardar = findViewById(R.id.btnGuardarProducto);

        dbHelper = new BDHelper(this);

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

            boolean res = dbHelper.agregarProducto(nombre, precio, imagen);
            if (res) {
                Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Error al agregar producto", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
