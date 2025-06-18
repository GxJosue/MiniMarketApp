package com.example.minimarketapp;

public class Producto {
    private int id;
    private String nombre;
    private double precio;
    private String imagen; // Ruta o URL, opcional para luego

    public Producto(int id, String nombre, double precio, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
    }

    // Getters y setters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public String getImagen() { return imagen; }

    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setImagen(String imagen) { this.imagen = imagen; }
}
