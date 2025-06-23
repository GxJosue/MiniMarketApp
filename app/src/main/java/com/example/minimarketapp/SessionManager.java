package com.example.minimarketapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void guardarUsuario(String usuario) {
        editor.putString("usuario", usuario);
        editor.apply();
    }

    public String obtenerUsuario() {
        return prefs.getString("usuario", null);
    }
    public void cerrarSesion() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

}
