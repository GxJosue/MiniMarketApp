package com.example.minimarketapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText usuario, correo, password;
    Button btnRegistrar;
    BDHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usuario = findViewById(R.id.txtUsuarioRegistro);
        correo = findViewById(R.id.txtCorreoRegistro);
        password = findViewById(R.id.txtPasswordRegistro);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        db = new BDHelper(this);

        btnRegistrar.setOnClickListener(v -> {
            String user = usuario.getText().toString();
            String email = correo.getText().toString().trim();
            String pass = password.getText().toString();

            // Validación de campos vacíos
            if (user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Campos vacíos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Usamos el nuevo método que devuelve el ID del usuario
            long userId = db.insertarUsuario(user, email, pass); // Este método debe retornar el ID insertado

            // Verificamos si la inserción fue exitosa (el ID no puede ser -1)
            if (userId != -1) {
                // Guardamos el ID de usuario en SharedPreferences
                SharedPreferences prefs = getSharedPreferences("MiPreferencia", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("user_id", (int) userId); // Guardamos el ID del usuario
                editor.putString("nombre_usuario", user);
                editor.putString("correo_usuario", email);
                editor.apply();

                Toast.makeText(this, "Registrado correctamente", Toast.LENGTH_SHORT).show();
                finish(); // Volver a la pantalla de Login
            } else {
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


