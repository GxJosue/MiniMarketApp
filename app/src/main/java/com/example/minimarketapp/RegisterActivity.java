package com.example.minimarketapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText usuario, correo, password;
    Button btnRegistrar;

    UsuarioDao usuarioDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usuario = findViewById(R.id.txtUsuarioRegistro);
        correo = findViewById(R.id.txtCorreoRegistro);
        password = findViewById(R.id.txtPasswordRegistro);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        usuarioDao = AppDatabase.getInstance(getApplicationContext()).usuarioDao();

        btnRegistrar.setOnClickListener(v -> {
            String user = usuario.getText().toString();
            String email = correo.getText().toString().trim();
            String pass = password.getText().toString();

            if (user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Campos vacíos", Toast.LENGTH_SHORT).show();
                return;
            }

            Usuario nuevo = new Usuario(user, email, pass);

            new Thread(() -> {
                long userId = usuarioDao.insertar(nuevo);
                if (userId != -1) {
                    SharedPreferences prefs = getSharedPreferences("MiPreferencia", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("user_id", (int) userId);
                    editor.putString("nombre_usuario", user);
                    editor.putString("correo_usuario", email);
                    editor.apply();

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Registrado correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }
}
