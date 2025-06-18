package com.example.minimarketapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText usuario, password;
    Button btnLogin, btnRegistro;
    BDHelper db;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuario = findViewById(R.id.txtUsuario);
        password = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnIrRegistro);

        db = new BDHelper(this);
        session = new SessionManager(this);

        btnLogin.setOnClickListener(v -> {
            String user = usuario.getText().toString();
            String pass = password.getText().toString();

            if (db.login(user, pass)) {
                session.guardarUsuario(user);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
                else {
                Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegistro.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}
