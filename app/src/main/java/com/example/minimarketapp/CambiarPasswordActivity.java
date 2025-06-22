package com.example.minimarketapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CambiarPasswordActivity extends AppCompatActivity {

    EditText etPasswordActual, etPasswordNueva, etConfirmarPassword;
    Button btnGuardarPassword;
    BDHelper db;  // Asumiendo que tienes BDHelper para interactuar con la base de datos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_password);

        etPasswordActual = findViewById(R.id.etPasswordActual);
        etPasswordNueva = findViewById(R.id.etPasswordNueva);
        etConfirmarPassword = findViewById(R.id.etConfirmarPassword);
        btnGuardarPassword = findViewById(R.id.btnGuardarPassword);

        db = new BDHelper(this);

        btnGuardarPassword.setOnClickListener(v -> {
            String passwordActual = etPasswordActual.getText().toString().trim();
            String passwordNueva = etPasswordNueva.getText().toString().trim();
            String confirmarPassword = etConfirmarPassword.getText().toString().trim();

            // Validaciones
            if (TextUtils.isEmpty(passwordActual) || TextUtils.isEmpty(passwordNueva) || TextUtils.isEmpty(confirmarPassword)) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!passwordNueva.equals(confirmarPassword)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificar si la contraseña actual es correcta (en la base de datos)
            boolean passwordCorrecta = db.verificarPassword(passwordActual);

            if (!passwordCorrecta) {
                Toast.makeText(this, "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show();
                return;
            }

            // Actualizar la contraseña en la base de datos
            boolean passwordActualizada = db.actualizarPassword(passwordNueva);

            if (passwordActualizada) {
                Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
                finish();  // Vuelve a la actividad anterior
            } else {
                Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
