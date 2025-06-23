package com.example.minimarketapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "pedidos")
public class Pedido {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "cliente")
    public String cliente;

    @ColumnInfo(name = "direccion")
    public String direccion;

    @ColumnInfo(name = "productos")
    public String productos;

    @ColumnInfo(name = "estado")
    public String estado;

    @ColumnInfo(name = "fecha")
    public long fecha;

    public Pedido(String cliente, String direccion, String productos, String estado, long fecha) {
        this.cliente = cliente;
        this.direccion = direccion;
        this.productos = productos;
        this.estado = estado;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }
}
