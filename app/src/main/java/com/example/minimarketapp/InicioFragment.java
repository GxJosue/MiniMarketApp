package com.example.minimarketapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class InicioFragment extends Fragment {

    public InicioFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        // 🔹 Mostrar saludo personalizado
        TextView tvSaludo = view.findViewById(R.id.tvSaludo);
        SessionManager session = new SessionManager(requireContext());
        String usuario = session.obtenerUsuario();

        if (usuario != null) {
            tvSaludo.setText("Bienvenido, " + usuario + "!");
        } else {
            tvSaludo.setText("Bienvenido!");
        }

        // 🔹 Botones
        Button btnProductos = view.findViewById(R.id.btnProductos);
        Button btnPedidos = view.findViewById(R.id.btnPedidos);
        Button btnPerfil = view.findViewById(R.id.btnPerfil);

        // 🔹 Abrir PerfilFragment
        btnPerfil.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new PerfilFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // 🔹 Abrir ProductosActivity (si ya está creada)
        btnProductos.setOnClickListener(v -> {
            startActivity(new android.content.Intent(getActivity(), ProductosActivity.class));
        });

        // 🔹 Pendiente: acción del botón Pedidos
        btnPedidos.setOnClickListener(v -> {
            // Por ahora puedes dejarlo vacío o poner un Toast
        });

        return view;
    }
}

