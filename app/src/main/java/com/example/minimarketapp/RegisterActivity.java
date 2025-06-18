package com.example.minimarketapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText usuario, password;
    Button btnRegistrar;
    BDHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usuario = findViewById(R.id.txtUsuarioRegistro);
        password = findViewById(R.id.txtPasswordRegistro);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        db = new BDHelper(this);

        btnRegistrar.setOnClickListener(v -> {
            String user = usuario.getText().toString();
            String pass = password.getText().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Campos vacíos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.registrarUsuario(user, pass)) {
                Toast.makeText(this, "Registrado correctamente", Toast.LENGTH_SHORT).show();
                finish(); // vuelve a login
            } else {
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
