package com.example.minimarketapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvUsuarioPerfil;
    private EditText etNombre;
    private TextView tvPending;
    private Button btnActualizarNombre, btnCerrarSesion;
    private SessionManager session;
    private UsuarioDao usuarioDao;
    private PendingUpdateDao pendingUpdateDao;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final String TAG = "PerfilActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mi Perfil");
        toolbar.setTitleTextColor(Color.WHITE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setTint(Color.WHITE);
        }

        tvUsuarioPerfil = findViewById(R.id.tvUsuarioPerfil);
        etNombre = findViewById(R.id.etNombre);
        tvPending = findViewById(R.id.tvPending);
        btnActualizarNombre = findViewById(R.id.btnActualizarNombre);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        session = new SessionManager(this);
        usuarioDao = AppDatabase.getInstance(getApplicationContext()).usuarioDao();
        pendingUpdateDao = AppDatabase.getInstance(getApplicationContext()).pendingUpdateDao();

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String usuario = session.obtenerUsuario();
        if (usuario != null) {
            tvUsuarioPerfil.setText("Correo electrónico: " + usuario);
            // Cargar nombre local
            new Thread(() -> {
                Usuario u = usuarioDao.obtenerPorUsuario(usuario);
                runOnUiThread(() -> {
                    if (u != null && u.getNombreCompleto() != null) {
                        etNombre.setText(u.getNombreCompleto());
                    }
                    // Mostrar si hay pendientes para este usuario
                    new Thread(() -> {
                        List<PendingUpdate> pendientes = pendingUpdateDao.obtenerPorUsuario(usuario);
                        runOnUiThread(() -> {
                            if (pendientes != null && !pendientes.isEmpty()) {
                                tvPending.setText("Tienes " + pendientes.size() + " actualización(es) pendientes por sincronizar");
                                tvPending.setVisibility(TextView.VISIBLE);
                            } else {
                                tvPending.setVisibility(TextView.GONE);
                            }
                        });
                    }).start();
                });
            }).start();
        } else {
            tvUsuarioPerfil.setText("Usuario: Invitado");
        }

        btnActualizarNombre.setOnClickListener(v -> {
            String nuevoNombre = etNombre.getText().toString().trim();
            if (nuevoNombre.length() < 7) {
                etNombre.setError("El nombre debe tener al menos 7 caracteres");
                etNombre.requestFocus();
                return;
            }

            if (usuario == null) {
                // Si no hay usuario en sesión, no se puede actualizar
                return;
            }

            if (isConnected()) {
                // Intentar actualizar en Firestore
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    // Intentar iniciar sesión en Firebase con credenciales locales (no guardamos la contraseña aquí)
                    // Si currentUser es nulo, no podemos actualizar en Firestore de forma segura; crearemos pendiente.
                    crearPendienteYActualizarLocal(usuario, nuevoNombre);
                    return;
                }
                String uid = currentUser.getUid();
                db.collection("users").document(uid)
                        .update("nombre", nuevoNombre)
                        .addOnSuccessListener(aVoid -> {
                            // Actualizar local
                            new Thread(() -> {
                                usuarioDao.actualizarNombre(usuario, nuevoNombre);
                                // eliminar pendientes relacionados si existieran
                                List<PendingUpdate> pendientes = pendingUpdateDao.obtenerPorUsuario(usuario);
                                for (PendingUpdate p : pendientes) {
                                    if ("nombre".equals(p.getCampo())) {
                                        pendingUpdateDao.eliminarPorId(p.getId());
                                    }
                                }
                                runOnUiThread(() -> {
                                    tvPending.setVisibility(TextView.GONE);
                                    android.widget.Toast.makeText(PerfilActivity.this, "Nombre actualizado", android.widget.Toast.LENGTH_SHORT).show();
                                });
                            }).start();
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error actualizando Firestore: " + e.getMessage(), e);
                            // Crear pendiente localmente y actualizar local DB
                            crearPendienteYActualizarLocal(usuario, nuevoNombre);
                        });
            } else {
                // Sin conexión -> marcar pendiente y actualizar local
                crearPendienteYActualizarLocal(usuario, nuevoNombre);
            }
        });

        btnCerrarSesion.setOnClickListener(v -> {
            session.cerrarSesion();
            // También cerrar sesión en Firebase si está
            if (mAuth != null) {
                mAuth.signOut();
            }
            Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Para limpiar el stack
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Intentar sincronizar pendientes cuando volvamos y tengamos usuario autenticado
        intentarSincronizarPendientes();
    }

    private void crearPendienteYActualizarLocal(String usuarioEmail, String nuevoNombre) {
        new Thread(() -> {
            // Insertar pendiente
            PendingUpdate pending = new PendingUpdate(usuarioEmail, "nombre", nuevoNombre, System.currentTimeMillis());
            pendingUpdateDao.insertar(pending);
            // Actualizar local en Usuario
            usuarioDao.actualizarNombre(usuarioEmail, nuevoNombre);

            // Actualizar UI
            runOnUiThread(() -> {
                tvPending.setText("La actualización quedará sincronizada cuando haya conexión");
                tvPending.setVisibility(TextView.VISIBLE);
                android.widget.Toast.makeText(PerfilActivity.this, "Cambio guardado localmente (pendiente)", android.widget.Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    // Sincroniza pendientes para el usuario autenticado actual
    private void intentarSincronizarPendientes() {
        new Thread(() -> {
            String usuarioEmail = session.obtenerUsuario();
            if (usuarioEmail == null) return;

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                // No hay usuario autenticado en Firebase, no podemos sincronizar
                return;
            }
            String uid = currentUser.getUid();

            List<PendingUpdate> pendientes = pendingUpdateDao.obtenerPorUsuario(usuarioEmail);
            if (pendientes == null || pendientes.isEmpty()) return;

            for (PendingUpdate p : pendientes) {
                if ("nombre".equals(p.getCampo())) {
                    String valor = p.getValor();
                    db.collection("users").document(uid)
                            .update("nombre", valor)
                            .addOnSuccessListener(aVoid -> {
                                // eliminar pendiente
                                new Thread(() -> {
                                    pendingUpdateDao.eliminarPorId(p.getId());
                                    runOnUiThread(() -> {
                                        tvPending.setVisibility(TextView.GONE);
                                        android.widget.Toast.makeText(PerfilActivity.this, "Pendiente sincronizado", android.widget.Toast.LENGTH_SHORT).show();
                                    });
                                }).start();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error sincronizando pendiente id=" + p.getId() + " : " + e.getMessage(), e);
                                // no eliminar; se intentará más adelante
                            });
                }
            }
        }).start();
    }

    // Método simple para verificar conexión de red
    private boolean isConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            if (cm == null) return false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
                return nc != null && (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            } else {
                NetworkInfo ni = cm.getActiveNetworkInfo();
                return ni != null && ni.isConnected();
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();  // Cierra esta actividad y vuelve a la anterior
        return true;
    }
}