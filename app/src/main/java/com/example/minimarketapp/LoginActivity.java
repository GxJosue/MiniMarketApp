package com.example.minimarketapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText txtUsuario, txtPassword;
    Button btnLogin, btnRegistro;
    SessionManager session;
    UsuarioDao usuarioDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        String usuarioGuardado = session.obtenerUsuario();

        if (usuarioGuardado != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Login");
            // No mostrar botón atrás
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        toolbar.setTitleTextColor(Color.WHITE);

        txtUsuario = findViewById(R.id.txtUsuario);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnIrRegistro);

        usuarioDao = AppDatabase.getInstance(getApplicationContext()).usuarioDao();

        btnLogin.setOnClickListener(v -> {
            String user = txtUsuario.getText().toString();
            String pass = txtPassword.getText().toString();

            new Thread(() -> {
                Usuario usuario = usuarioDao.login(user, pass);
                runOnUiThread(() -> {
                    if (usuario != null) {
                        session.guardarUsuario(user);
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });

        btnRegistro.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}

