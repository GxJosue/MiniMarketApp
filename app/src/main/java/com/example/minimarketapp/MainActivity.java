package com.example.minimarketapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    SessionManager session;
    TextView tvSaludo;
    Button btnProductos, btnPedidos, btnPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Mini Market App");
        toolbar.setTitleTextColor(Color.WHITE);

        tvSaludo = findViewById(R.id.tvSaludo);
        btnProductos = findViewById(R.id.btnProductos);
        btnPedidos = findViewById(R.id.btnPedidos);
        btnPerfil = findViewById(R.id.btnPerfil);

        session = new SessionManager(this);

        // Cargar fragmento de inicio
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new InicioFragment())
                    .commit();
        }

        btnProductos.setOnClickListener(v -> {
            startActivity(new Intent(this, ProductosActivity.class));
        });

        btnPedidos.setOnClickListener(v -> {
            startActivity(new Intent(this, PedidosActivity.class));
        });

        btnPerfil.setOnClickListener(v -> {
            // Aquí puedes reemplazar con el fragmento si lo estás usando
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new PerfilFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}

