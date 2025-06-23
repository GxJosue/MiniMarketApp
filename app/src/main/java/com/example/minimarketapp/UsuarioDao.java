package com.example.minimarketapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UsuarioDao {
    @Insert
    void insertar(Usuario usuario);

    @Query("SELECT * FROM usuarios WHERE usuario = :usuario AND password = :password LIMIT 1")
    Usuario login(String usuario, String password);

    @Query("SELECT * FROM usuarios WHERE usuario = :usuario LIMIT 1")
    Usuario obtenerPorUsuario(String usuario);
}
