package com.example.minimarketapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 1000; // 2 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Instalar compat splash screen antes de super.onCreate
        try {
            SplashScreen.installSplashScreen(this);
        } catch (NoClassDefFoundError e) {
            // Si no existe la clase (no añadiste la dependencia), ignorar
        }

        super.onCreate(savedInstanceState);

        // Inflar un layout mínimo que tiene el mismo background que @drawable/splash_background
        // para evitar cualquier parpadeo o doble splash.
        setContentView(R.layout.activity_splash);

        // Esperar y lanzar LoginActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY_MS);
    }
}