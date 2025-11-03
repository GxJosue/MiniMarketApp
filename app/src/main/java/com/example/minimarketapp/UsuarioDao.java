package com.example.minimarketapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UsuarioDao {
    @Insert
    void insertar(Usuario usuario);

    @Query("SELECT * FROM usuarios WHERE usuario = :usuario AND password = :password LIMIT 1")
    Usuario login(String usuario, String password);

    @Query("SELECT * FROM usuarios WHERE usuario = :usuario LIMIT 1")
    Usuario obtenerPorUsuario(String usuario);

    // Nuevo: actualizar nombre completo para un usuario identificado por su email (campo 'usuario')
    @Query("UPDATE usuarios SET nombreCompleto = :nombre WHERE usuario = :usuario")
    void actualizarNombre(String usuario, String nombre);

    // Nuevo: obtener todos (útil para debug)
    @Query("SELECT * FROM usuarios")
    List<Usuario> obtenerTodos();
}