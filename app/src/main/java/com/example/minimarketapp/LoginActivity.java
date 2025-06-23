package com.example.minimarketapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText txtUsuario, txtPassword;
    Button btnLogin, btnRegistro;
    UsuarioDao usuarioDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("MiPreferencia", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("user_id", -1);

        if (idUsuario != -1) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        txtUsuario = findViewById(R.id.txtUsuario);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnIrRegistro);

        usuarioDao = AppDatabase.getInstance(getApplicationContext()).usuarioDao();

        btnLogin.setOnClickListener(v -> {
            String user = txtUsuario.getText().toString();
            String pass = txtPassword.getText().toString();

            new Thread(() -> {
                Usuario u = usuarioDao.login(user, pass);
                if (u != null) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("user_id", u.id);
                    editor.putString("nombre_usuario", u.usuario);
                    editor.putString("correo_usuario", u.correo);
                    editor.apply();

                    runOnUiThread(() -> {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });

        btnRegistro.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}
