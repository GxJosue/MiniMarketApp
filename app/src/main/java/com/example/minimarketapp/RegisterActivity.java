package com.example.minimarketapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText usuario, password;
    Button btnRegistrar;
    UsuarioDao usuarioDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usuario = findViewById(R.id.txtUsuarioRegistro);
        password = findViewById(R.id.txtPasswordRegistro);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        usuarioDao = AppDatabase.getInstance(getApplicationContext()).usuarioDao();

        btnRegistrar.setOnClickListener(v -> {
            String user = usuario.getText().toString();
            String pass = password.getText().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Campos vacíos", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                Usuario existente = usuarioDao.obtenerPorUsuario(user);
                runOnUiThread(() -> {
                    if (existente != null) {
                        Toast.makeText(this, "Usuario ya existe", Toast.LENGTH_SHORT).show();
                    } else {
                        new Thread(() -> {
                            usuarioDao.insertar(new Usuario(user, pass));
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Registrado correctamente", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }).start();
                    }
                });
            }).start();
        });
    }
}

