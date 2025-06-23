package com.example.minimarketapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import com.example.minimarketapp.R;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class PerfilFragment extends Fragment {

    private UsuarioDao usuarioDao;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        TextView txtUserName = view.findViewById(R.id.txtUserName);
        TextView txtUserEmail = view.findViewById(R.id.txtUserEmail);
        Button btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        Button btnCambiarPassword = view.findViewById(R.id.btnCambiarPassword);

        Context context = requireContext();
        usuarioDao = AppDatabase.getInstance(context).usuarioDao();
        prefs = context.getSharedPreferences("MiPreferencia", Context.MODE_PRIVATE);

        String nombre = prefs.getString("nombre_usuario", "Invitado");
        String correo = prefs.getString("correo_usuario", "correo@example.com");
        int userId = prefs.getInt("user_id", -1);

        txtUserName.setText(nombre);
        txtUserEmail.setText(correo);

        btnCerrarSesion.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(context, LoginActivity.class));
            requireActivity().finish();
        });

        btnCambiarPassword.setOnClickListener(v -> mostrarDialogoCambiarPassword(userId));

        return view;
    }

    private void mostrarDialogoCambiarPassword(int userId) {
        LayoutInflater inflaterDialog = LayoutInflater.from(getContext());
        View dialogView = inflaterDialog.inflate(R.layout.dialog_cambiar_password, null);

        EditText etActual = dialogView.findViewById(R.id.etPasswordActual);
        EditText etNueva = dialogView.findViewById(R.id.etNuevaPassword);
        EditText etConfirmar = dialogView.findViewById(R.id.etConfirmarPassword);

        new AlertDialog.Builder(getContext())
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

                    if (!nueva.equals(confirmar)) {
                        Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    new Thread(() -> {
                        Usuario usuario = usuarioDao.verificarPassword(userId, actual);
                        if (usuario != null) {
                            int result = usuarioDao.actualizarPassword(userId, nueva);
                            requireActivity().runOnUiThread(() -> {
                                if (result > 0) {
                                    Toast.makeText(getContext(), "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "No se pudo actualizar", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show()
                            );
                        }
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}




