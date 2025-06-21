package com.example.minimarketapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText txtUsuario, txtPassword;
    Button btnLogin, btnRegistro;
    BDHelper db;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        String usuarioGuardado = session.obtenerUsuario();

        if (usuarioGuardado != null) {
            // Ya hay una sesión activa, ir directamente a MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return; // 👈 para evitar seguir ejecutando el resto
        }

        setContentView(R.layout.activity_login);

        txtUsuario = findViewById(R.id.txtUsuario);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnIrRegistro);

        db = new BDHelper(this);
        session = new SessionManager(this);

        btnLogin.setOnClickListener(v -> {
            String user = txtUsuario.getText().toString();
            String pass = txtPassword.getText().toString();

            if (db.login(user, pass)) {
                session.guardarUsuario(user); // ✔ guardamos el nombre de usuario
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegistro.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}
