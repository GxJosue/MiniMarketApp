package com.example.minimarketapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "productos")
public class Producto {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String nombre;
    private double precio;
    private String imagen;

    public Producto(String nombre, double precio, String imagen) {
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
}
