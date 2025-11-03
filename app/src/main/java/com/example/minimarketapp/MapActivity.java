package com.example.minimarketapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MapActivity extends AppCompatActivity {
    private static final String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Toolbar toolbar = findViewById(R.id.toolbarMap);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Ubicación");
            toolbar.setTitleTextColor(Color.WHITE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setTint(Color.WHITE);
        }

        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("lat",13.9770759);
        double lng = intent.getDoubleExtra("lng", -89.5619125);

        Log.d(TAG, "MapActivity recibio lat=" + lat + " lng=" + lng);
        Toast.makeText(this, "Abriendo mapa en: " + lat + ", " + lng, Toast.LENGTH_SHORT).show();

        if (savedInstanceState == null) {
            ComercioMapFragment fragment = ComercioMapFragment.newInstance(lat, lng);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_map_host, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}