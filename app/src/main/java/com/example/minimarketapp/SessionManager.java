package com.example.minimarketapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "session";
    private static final String KEY_USUARIO = "usuario";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private static volatile String tempUser = null;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }


    public void guardarUsuario(String usuario) {
        editor.putString(KEY_USUARIO, usuario);
        editor.apply();
        tempUser = usuario;
    }


    public void setTempUser(String usuario) {
        tempUser = usuario;
    }


    public String obtenerUsuario() {
        String persistente = prefs.getString(KEY_USUARIO, null);
        if (persistente != null) {
            return persistente;
        }
        return tempUser;
    }


    public void cerrarSesion() {
        editor.clear();
        editor.apply();
        tempUser = null;
    }
}