package com.example.minimarketapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ComercioMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_LAT = "arg_lat";
    private static final String ARG_LNG = "arg_lng";

    // Valores por defecto (si no se pasan args)
    private static final double DEFAULT_LAT = 13.9770759;
    private static final double DEFAULT_LNG = -89.5619125;

    private double latitude = DEFAULT_LAT;
    private double longitude = DEFAULT_LNG;

    private GoogleMap googleMap;

    public ComercioMapFragment() { /* requerido */ }

    public static ComercioMapFragment newInstance(double lat, double lng) {
        ComercioMapFragment fragment = new ComercioMapFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LAT, lat);
        args.putDouble(ARG_LNG, lng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        if (getArguments() != null) {
            latitude = getArguments().getDouble(ARG_LAT, DEFAULT_LAT);
            longitude = getArguments().getDouble(ARG_LNG, DEFAULT_LNG);
        }

        // Cargar SupportMapFragment dinámicamente
        SupportMapFragment mapFragment = new SupportMapFragment();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commitNowAllowingStateLoss();

        mapFragment.getMapAsync(this);

        return root;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;

        LatLng comercio = new LatLng(latitude, longitude);

        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
                .position(comercio)
                .title("MiniMarket App - Comercio"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(comercio, 15f));

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
    }
}