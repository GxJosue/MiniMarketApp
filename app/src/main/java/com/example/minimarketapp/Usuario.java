package com.example.minimarketapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuarios")
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Aquí usamos 'usuario' para almacenar el correo electrónico
    public String usuario;
    public String password;

    // Nuevo campo: nombre completo del usuario
    public String nombreCompleto;

    // Nuevo constructor con nombre completo
    public Usuario(String usuario, String password, String nombreCompleto) {
        this.usuario = usuario;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
    }

    public int getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getPassword() { return password; }
    public String getNombreCompleto() { return nombreCompleto; }
}