package com.example.minimarketapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * LoginActivity actualizado para:
 * - Intentar autenticación contra Firebase primero.
 * - Si Firebase responde OK: obtener perfil desde Firestore, guardar/actualizar en Room y continuar.
 * - Si Firebase falla por red: fallback a autenticación local (Room).
 * - Si Firebase falla por credenciales inválidas: mostrar error.
 *
 * Pega este archivo reemplazando tu LoginActivity actual.
 */
public class LoginActivity extends AppCompatActivity {
    EditText txtUsuario, txtPassword;
    Button btnLogin, btnRegistro;
    CheckBox cbRemember;
    SessionManager session;
    UsuarioDao usuarioDao;

    // FirebaseAuth para intentar iniciar sesión en la nube
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Regex: al menos una letra, al menos un dígito, longitud mínima 8, solo letras y dígitos
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        String usuarioGuardado = session.obtenerUsuario();

        if (usuarioGuardado != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Login");
            // No mostrar botón atrás
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        toolbar.setTitleTextColor(Color.WHITE);

        txtUsuario = findViewById(R.id.txtUsuario);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnIrRegistro);
        cbRemember = findViewById(R.id.cbRemember);

        usuarioDao = AppDatabase.getInstance(getApplicationContext()).usuarioDao();

        // Inicializar FirebaseAuth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> {
            String user = txtUsuario.getText().toString().trim();
            String pass = txtPassword.getText().toString().trim();

            // Validaciones básicas
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
                txtUsuario.setError("Introduce un correo válido");
                txtUsuario.requestFocus();
                return;
            }
            if (!pass.matches(PASSWORD_REGEX)) {
                txtPassword.setError("Contraseña mínima 8 caracteres alfanuméricos");
                txtPassword.requestFocus();
                return;
            }

            // Intentar login contra Firebase primero
            attemptFirebaseLogin(user, pass);
        });

        btnRegistro.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    /**
     * Intentar iniciar sesión contra Firebase. En caso de éxito, obtener perfil desde Firestore,
     * guardar/actualizar en Room y proceder al MainActivity.
     * En caso de fallo por red, intentar autenticación local (Room).
     * En caso de fallo por credenciales inválidas, informar al usuario.
     */
    private void attemptFirebaseLogin(String email, String password) {
        Log.d(TAG, "Attempting Firebase signIn for " + email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Firebase signIn successful");
                        // Recuperar datos del usuario en Firestore (users/{uid})
                        AuthResult authResult = task.getResult();
                        String uid = (authResult != null && authResult.getUser() != null)
                                ? authResult.getUser().getUid()
                                : null;

                        if (uid != null) {
                            db.collection("users").document(uid).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        handleFirestoreUserAndProceed(email, password, documentSnapshot);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Failed to read Firestore user: " + e.getMessage(), e);
                                        // Aunque falle la lectura de Firestore, podemos crear/actualizar localmente
                                        handleFirestoreUserAndProceed(email, password, null);
                                    });
                        } else {
                            // Muy raro: uid nulo. Proceder a Main guardando al menos sesión.
                            runOnUiThread(() -> {
                                session.setTempUser(email);
                                if (cbRemember != null && cbRemember.isChecked()) session.guardarUsuario(email);
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            });
                        }
                    } else {
                        Exception e = task.getException();
                        String msg = e != null ? e.getMessage() : "Unknown error";
                        Log.w(TAG, "Firebase signIn failed: " + msg, e);

                        // Si la falla es por red, fallback a autenticación local
                        if (e instanceof FirebaseNetworkException || (msg != null && msg.toLowerCase().contains("network"))) {
                            Log.d(TAG, "Firebase network error - trying local auth");
                            attemptLocalLoginFallback(email, password);
                        } else {
                            // Credenciales inválidas u otro error no-network: informar
                            runOnUiThread(() -> Toast.makeText(this, "Error en login: " + msg, Toast.LENGTH_LONG).show());
                        }
                    }
                });
    }

    /**
     * Inserta/actualiza el usuario en la DB local (Room) usando los datos de Firestore si están,
     * y luego continúa a MainActivity guardando sesión según el checkbox.
     */
    private void handleFirestoreUserAndProceed(String email, String password, DocumentSnapshot documentSnapshot) {
        String nombre = "";
        if (documentSnapshot != null && documentSnapshot.exists()) {
            String n = documentSnapshot.getString("nombre");
            if (n != null) nombre = n;
        }
        final String finalNombre = nombre;
        Log.d(TAG, "Firestore user fetched: name='" + finalNombre + "' for email=" + email);

        // Guardar/actualizar en Room en background
        new Thread(() -> {
            try {
                Usuario usuario = new Usuario(email, password, finalNombre);
                usuarioDao.insertar(usuario); // Asegúrate que @Insert tenga onConflict = REPLACE para evitar duplicados
                Log.d(TAG, "Local DB: usuario insertado/actualizado para " + email);
            } catch (Exception ex) {
                Log.w(TAG, "Error guardando usuario en Room: " + ex.getMessage(), ex);
            }

            // Continuar en UI thread: guardar sesión y abrir MainActivity
            runOnUiThread(() -> {
                session.setTempUser(email);
                if (cbRemember != null && cbRemember.isChecked()) {
                    session.guardarUsuario(email); // Si tu SessionManager guarda también contraseña, ajústalo aquí
                }
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            });
        }).start();
    }

    /**
     * Intentar autenticación local consultando Room (solo cuando no hay red).
     */
    private void attemptLocalLoginFallback(String email, String password) {
        new Thread(() -> {
            try {
                Usuario usuarioLocal = usuarioDao.login(email, password);
                if (usuarioLocal != null) {
                    Log.d(TAG, "Local login OK for " + email);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Login offline OK (datos locales)", Toast.LENGTH_SHORT).show();
                        session.setTempUser(email);
                        if (cbRemember != null && cbRemember.isChecked()) session.guardarUsuario(email);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    });
                } else {
                    Log.d(TAG, "Local login failed - user not found or password mismatch");
                    runOnUiThread(() -> Toast.makeText(this, "No hay conexión y credenciales locales no coinciden", Toast.LENGTH_LONG).show());
                }
            } catch (Exception ex) {
                Log.w(TAG, "Error checking local DB: " + ex.getMessage(), ex);
                runOnUiThread(() -> Toast.makeText(this, "Error interno al autenticar localmente", Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}