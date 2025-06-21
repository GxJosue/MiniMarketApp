package com.example.minimarketapp;

public class Pedido {
    int id;
    String cliente;
    String direccion;
    String productos;
    String estado;
    long fecha;

    public Pedido(int id, String cliente, String direccion, String productos, String estado, long fecha) {
        this.id = id;
        this.cliente = cliente;
        this.direccion = direccion;
        this.productos = productos;
        this.estado = estado;
        this.fecha = fecha;
    }

    public int getId() { return id; }

}
