package com.example.minimarketapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView tvSaludo;
    Button btnProductos, btnPedidos, btnPerfil;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSaludo = findViewById(R.id.tvSaludo);
        btnProductos = findViewById(R.id.btnProductos);
        btnPedidos = findViewById(R.id.btnPedidos);
        btnPerfil = findViewById(R.id.btnPerfil);

        session = new SessionManager(this);
        String usuario = session.obtenerUsuario();

        // Mostrar saludo con el nombre de usuario
        if (usuario != null) {
            tvSaludo.setText("Bienvenido, " + usuario + "!");
        } else {
            tvSaludo.setText("Bienvenido!");
        }

        btnProductos.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductosActivity.class);
            startActivity(intent);
        });


        btnPedidos.setOnClickListener(v -> {
            // Aquí iría la pantalla de pedidos (a crear luego)
            //startActivity(new Intent(this, PedidosActivity.class));
        });

        btnPerfil.setOnClickListener(v -> {
            // Aquí iría la pantalla de perfil (a crear luego)
            // startActivity(new Intent(this, PerfilActivity.class));
        });
    }
}
