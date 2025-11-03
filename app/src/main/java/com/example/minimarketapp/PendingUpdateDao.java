package com.example.minimarketapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PendingUpdateDao {

    @Insert
    void insertar(PendingUpdate pendingUpdate);

    @Query("SELECT * FROM pending_updates ORDER BY fecha ASC")
    List<PendingUpdate> obtenerTodos();

    @Query("SELECT * FROM pending_updates WHERE usuario = :usuario ORDER BY fecha ASC")
    List<PendingUpdate> obtenerPorUsuario(String usuario);

    @Delete
    void eliminar(PendingUpdate pendingUpdate);

    @Query("DELETE FROM pending_updates WHERE id = :id")
    void eliminarPorId(int id);
}