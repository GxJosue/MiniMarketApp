package com.example.minimarketapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuarios")
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String usuario;
    public String password;

    public Usuario(String usuario, String password) {
        this.usuario = usuario;
        this.password = password;
    }

    public int getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getPassword() { return password; }
}
