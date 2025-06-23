package com.example.minimarketapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvUsuarioPerfil;
    private Button btnCerrarSesion;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mi Perfil");
        toolbar.setTitleTextColor(Color.WHITE);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setTint(Color.WHITE);
        }


        tvUsuarioPerfil = findViewById(R.id.tvUsuarioPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        session = new SessionManager(this);

        String usuario = session.obtenerUsuario();
        if (usuario != null) {
            tvUsuarioPerfil.setText("Usuario: " + usuario);
        } else {
            tvUsuarioPerfil.setText("Usuario: Invitado");
        }

        btnCerrarSesion.setOnClickListener(v -> {
            session.cerrarSesion();
            Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Para limpiar el stack
            startActivity(intent);
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();  // Cierra esta actividad y vuelve a la anterior
        return true;
    }

}
