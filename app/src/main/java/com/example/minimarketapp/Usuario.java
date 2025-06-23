package com.example.minimarketapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuarios")
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "usuario")
    public String usuario;

    @ColumnInfo(name = "correo")
    public String correo;

    @ColumnInfo(name = "password")
    public String password;

    public Usuario() {}

    @Ignore
    public Usuario(String usuario, String correo, String password) {
        this.usuario = usuario;
        this.correo = correo;
        this.password = password;
    }
}

