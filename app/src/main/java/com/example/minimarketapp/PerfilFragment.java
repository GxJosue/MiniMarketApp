package com.example.minimarketapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PerfilFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        // Referencias UI
        TextView txtUserName = view.findViewById(R.id.txtUserName);
        TextView txtUserEmail = view.findViewById(R.id.txtUserEmail);
        Button btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        Button btnCambiarPassword = view.findViewById(R.id.btnCambiarPassword);
        ImageView imgAvatar = view.findViewById(R.id.imgAvatar);

        // Cargar usuario desde SharedPreferences
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("MiPreferencia", Context.MODE_PRIVATE);
            String nombre = prefs.getString("nombre_usuario", "Invitado");
            String correo = prefs.getString("correo_usuario", "correo@example.com");

            txtUserName.setText(nombre);
            txtUserEmail.setText(correo);
        }

        // Botón cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            if (getActivity() != null) {
                SharedPreferences prefs = getActivity().getSharedPreferences("MiPreferencia", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear(); // Borra todo
                editor.apply();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish(); // Cierra MainActivity para no regresar
            }
        });

        btnCambiarPassword.setOnClickListener(v -> {
            LayoutInflater inflaterDialog = LayoutInflater.from(getContext());
            View dialogView = inflaterDialog.inflate(R.layout.dialog_cambiar_password, null);

            EditText etActual = dialogView.findViewById(R.id.etPasswordActual);
            EditText etNueva = dialogView.findViewById(R.id.etNuevaPassword);
            EditText etConfirmar = dialogView.findViewById(R.id.etConfirmarPassword);

            new android.app.AlertDialog.Builder(getContext())
                    .setTitle("Cambiar contraseña")
                    .setView(dialogView)
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        String actual = etActual.getText().toString();
                        String nueva = etNueva.getText().toString();
                        String confirmar = etConfirmar.getText().toString();

                        if (actual.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
                            Toast.makeText(getContext(), "Campos vacíos", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        BDHelper db = new BDHelper(getContext());

                        if (!db.verificarPassword(actual)) {
                            Toast.makeText(getContext(), "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!nueva.equals(confirmar)) {
                            Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (db.actualizarPassword(nueva)) {
                            Toast.makeText(getContext(), "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error al actualizar contraseña", Toast.LENGTH_SHORT).show();
                        }

                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        return view;
    }
}



