package com.example.minimarketapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PedidoDao {

    @Insert
    void insertar(Pedido pedido);

    @Query("SELECT * FROM pedidos ORDER BY fecha DESC")
    List<Pedido> obtenerTodos();
}

