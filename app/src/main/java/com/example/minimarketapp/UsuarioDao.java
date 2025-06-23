package com.example.minimarketapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UsuarioDao {

    @Insert
    long insertar(Usuario usuario);

    @Query("SELECT * FROM usuarios WHERE usuario = :usuario AND password = :password LIMIT 1")
    Usuario login(String usuario, String password);

    @Query("SELECT * FROM usuarios WHERE id = :id AND password = :passwordActual LIMIT 1")
    Usuario verificarPassword(int id, String passwordActual);

    @Query("UPDATE usuarios SET password = :nuevaPassword WHERE id = :id")
    int actualizarPassword(int id, String nuevaPassword);
}

