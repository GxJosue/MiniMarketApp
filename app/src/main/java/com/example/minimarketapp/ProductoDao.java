package com.example.minimarketapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface ProductoDao {

    @Insert
    void insertar(Producto producto);

    @Update
    int actualizarProducto(Producto producto);

    @Delete
    int eliminarProducto(Producto producto);

    @Query("SELECT * FROM productos")
    List<Producto> obtenerTodos();
}
