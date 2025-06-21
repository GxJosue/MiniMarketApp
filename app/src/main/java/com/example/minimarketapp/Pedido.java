package com.example.minimarketapp;

public class Pedido {
    int id;
    String cliente;
    String producto;
    String estado;
    long fecha;

    public Pedido(int id, String cliente, String producto, String estado, long fecha) {
        this.id = id;
        this.cliente = cliente;
        this.producto = producto;
        this.estado = estado;
        this.fecha = fecha;
    }

    public int getId() { return id; }
    public String getCliente() { return cliente; }
    public String getEstado() { return estado; }
    public long getFecha() {
        return fecha;
    }

}
