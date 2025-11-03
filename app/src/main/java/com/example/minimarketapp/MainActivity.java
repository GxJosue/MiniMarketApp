package com.example.minimarketapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.graphics.drawable.Drawable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.content.ContextCompat;

// WorkManager imports
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.appcheck.interop.BuildConfig;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView tvSaludo;
    Button btnProductos, btnPedidos, btnPerfil;
    SessionManager session;
    UsuarioDao usuarioDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // --- insertar inmediatamente después de: setSupportActionBar(toolbar);
        try {
            Drawable overflow = toolbar.getOverflowIcon();
            if (overflow != null) {
                Drawable wrapped = DrawableCompat.wrap(overflow);
                DrawableCompat.setTint(wrapped, Color.WHITE);
                toolbar.setOverflowIcon(wrapped);
            } else {
                // Opcional: si prefieres forzar un drawable blanco (añádelo a res/drawable si no existe)
                // Drawable white = ContextCompat.getDrawable(this, R.drawable.ic_more_vert_white_24dp);
                // if (white != null) toolbar.setOverflowIcon(white);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        toolbar.setTitle("Mini Market App");
        toolbar.setTitleTextColor(Color.WHITE);

        tvSaludo = findViewById(R.id.tvSaludo);
        btnProductos = findViewById(R.id.btnProductos);
        btnPedidos = findViewById(R.id.btnPedidos);
        btnPerfil = findViewById(R.id.btnPerfil);

        session = new SessionManager(this);
        usuarioDao = AppDatabase.getInstance(getApplicationContext()).usuarioDao();

        String usuarioEmail = session.obtenerUsuario();

        // Mostrar saludo: intentamos obtener el nombre completo desde la DB (Room).
        if (usuarioEmail != null) {
            // Mostrar provisionalmente el email hasta que la consulta termine (mejor UX)
            tvSaludo.setText("Bienvenido, " + usuarioEmail + "!");
            // Consultar en background la información del usuario
            new Thread(() -> {
                Usuario usuarioObj = usuarioDao.obtenerPorUsuario(usuarioEmail);
                if (usuarioObj != null) {
                    String nombreCompleto = usuarioObj.getNombreCompleto();
                    if (nombreCompleto != null && !nombreCompleto.trim().isEmpty()) {
                        // Actualizar UI en el hilo principal
                        runOnUiThread(() -> tvSaludo.setText("Bienvenido, " + nombreCompleto + "!"));
                        return;
                    }
                }
                // Si no hay usuario en DB o no tiene nombre, ya dejamos el email (o "Bienvenido!")
                // (no hace falta cambiar)
            }).start();
        } else {
            tvSaludo.setText("Bienvenido!");
        }

        btnProductos.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductosActivity.class);
            startActivity(intent);
        });

        btnPedidos.setOnClickListener(v -> {
            startActivity(new Intent(this, PedidosActivity.class));
        });

        btnPerfil.setOnClickListener(v -> {
            startActivity(new Intent(this, PerfilActivity.class));
        });

        // dentro de onCreate(), junto a los otros botones:
        double comercioLat = 13.9770759;
        double comercioLng = -89.5619125;

        Button btnMapa = findViewById(R.id.btnMapa);
        btnMapa.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("lat", comercioLat);
            intent.putExtra("lng", comercioLng);
            startActivity(intent);
        });

        // --- Programar WorkManager para sincronizar pendientes ---
        // Constraints: requiere conexión de red
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // PeriodicWorkRequest: mínimo 15 minutos permitido por WorkManager
        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest.Builder(SyncWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        // Enqueue único (si ya existe, KEEP la existente)
        WorkManager.getInstance(getApplicationContext())
                .enqueueUniquePeriodicWork("sync_pending_work", ExistingPeriodicWorkPolicy.KEEP, syncRequest);

        // --- EN TUS PRUEBAS: ejecutar una tarea one-time inmediata para forzar sincronización ---
        // Esto solo se ejecuta en DEBUG y es útil para verificar la sincronización sin esperar 15 minutos.
        // Puedes eliminar o comentar este bloque en producción.
        if (BuildConfig.DEBUG) {
            OneTimeWorkRequest immediateSync = new OneTimeWorkRequest.Builder(SyncWorker.class)
                    .setConstraints(constraints)
                    .build();
            WorkManager.getInstance(getApplicationContext()).enqueue(immediateSync);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflar menú
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Manejo del menu
        int id = item.getItemId();
        if (id == R.id.action_perfil) {
            startActivity(new Intent(this, PerfilActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}