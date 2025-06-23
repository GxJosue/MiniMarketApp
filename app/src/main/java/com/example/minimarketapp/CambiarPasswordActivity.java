package com.example.minimarketapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;

public class CambiarPasswordActivity extends AppCompatActivity {

    EditText etPasswordActual, etPasswordNueva, etConfirmarPassword;
    Button btnGuardarPassword;

    private UsuarioDao usuarioDao;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_password);

        etPasswordActual = findViewById(R.id.etPasswordActual);
        etPasswordNueva = findViewById(R.id.etPasswordNueva);
        etConfirmarPassword = findViewById(R.id.etConfirmarPassword);
        btnGuardarPassword = findViewById(R.id.btnGuardarPassword);

        usuarioDao = AppDatabase.getInstance(getApplicationContext()).usuarioDao();

        SharedPreferences prefs = getSharedPreferences("MiPreferencia", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        btnGuardarPassword.setOnClickListener(v -> {
            String passwordActual = etPasswordActual.getText().toString().trim();
            String passwordNueva = etPasswordNueva.getText().toString().trim();
            String confirmarPassword = etConfirmarPassword.getText().toString().trim();

            if (TextUtils.isEmpty(passwordActual) || TextUtils.isEmpty(passwordNueva) || TextUtils.isEmpty(confirmarPassword)) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!passwordNueva.equals(confirmarPassword)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userId == -1) {
                Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
                return;
            }

            Executors.newSingleThreadExecutor().execute(() -> {
                Usuario usuario = usuarioDao.verificarPassword(userId, passwordActual);

                if (usuario == null) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                int filasActualizadas = usuarioDao.actualizarPassword(userId, passwordNueva);
                runOnUiThread(() -> {
                    if (filasActualizadas > 0) {
                        Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }
}
