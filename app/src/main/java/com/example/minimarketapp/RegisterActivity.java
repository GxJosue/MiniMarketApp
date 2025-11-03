package com.example.minimarketapp;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText txtEmail, txtNombre, txtPassword;
    Button btnRegistrar;
    UsuarioDao usuarioDao;

    private static final String TAG = "RegisterActivity";
    // Misma regla de contraseña que en Login (al menos 8, una letra y un número)
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbarRegistrar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Registrar");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setTint(getResources().getColor(android.R.color.white));
        }

        txtEmail = findViewById(R.id.txtEmailRegistro);
        txtNombre = findViewById(R.id.txtNombreRegistro);
        txtPassword = findViewById(R.id.txtPasswordRegistro);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        usuarioDao = AppDatabase.getInstance(getApplicationContext()).usuarioDao();

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegistrar.setOnClickListener(v -> {
            String email = txtEmail.getText().toString().trim();
            String nombre = txtNombre.getText().toString().trim();
            String pass = txtPassword.getText().toString().trim();

            // Validaciones locales
            if (email.isEmpty() || nombre.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                txtEmail.setError("Introduce un correo válido");
                txtEmail.requestFocus();
                return;
            }

            if (nombre.length() < 7) {
                txtNombre.setError("El nombre debe tener al menos 7 caracteres");
                txtNombre.requestFocus();
                return;
            }

            if (!pass.matches(PASSWORD_REGEX)) {
                txtPassword.setError("Contraseña mínima 8 caracteres alfanuméricos");
                txtPassword.requestFocus();
                return;
            }

            // Comprobar si ya existe localmente
            new Thread(() -> {
                Usuario existente = usuarioDao.obtenerPorUsuario(email);
                runOnUiThread(() -> {
                    if (existente != null) {
                        Toast.makeText(this, "Usuario ya existe", Toast.LENGTH_SHORT).show();
                    } else {
                        // Crear usuario en Firebase Authentication
                        mAuth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                        if (firebaseUser == null) {
                                            Toast.makeText(this, "Registro falló: usuario Firebase es nulo", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        String uid = firebaseUser.getUid();

                                        // Guardar información adicional en Firestore en users/{uid}
                                        Map<String, Object> userMap = new HashMap<>();
                                        userMap.put("email", email);
                                        userMap.put("nombre", nombre);
                                        userMap.put("uid", uid);

                                        db.collection("users").document(uid)
                                                .set(userMap)
                                                .addOnSuccessListener(aVoid -> {
                                                    // Insertar también en la DB local (Room) en hilo separado
                                                    new Thread(() -> {
                                                        usuarioDao.insertar(new Usuario(email, pass, nombre));
                                                        runOnUiThread(() -> {
                                                            Toast.makeText(this, "Registrado correctamente", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        });
                                                    }).start();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e(TAG, "Error guardando en Firestore: " + e.getMessage(), e);
                                                    // Si falla Firestore, eliminamos el usuario de Auth para no dejar registro inconsistente
                                                    if (mAuth.getCurrentUser() != null) {
                                                        mAuth.getCurrentUser().delete()
                                                                .addOnCompleteListener(delTask -> {
                                                                    if (!delTask.isSuccessful()) {
                                                                        Log.e(TAG, "No se pudo eliminar usuario de Auth tras fallo Firestore");
                                                                    }
                                                                });
                                                    }
                                                    Toast.makeText(this, "Error al guardar en la nube: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                });

                                    } else {
                                        String msg = task.getException() != null ? task.getException().getMessage() : "Error en registro";
                                        Log.e(TAG, "createUserWithEmailAndPassword fallo: " + msg);
                                        Toast.makeText(this, "Error en registro: " + msg, Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });
            }).start();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}