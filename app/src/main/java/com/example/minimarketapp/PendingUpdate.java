package com.example.minimarketapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pending_updates")
public class PendingUpdate {

    @PrimaryKey(autoGenerate = true)
    public int id;

    // email del usuario al que aplica la actualización
    public String usuario;

    // nombre del campo a actualizar
    public String campo;

    // nuevo valor
    public String valor;

    // timestamp en ms
    public long fecha;

    public PendingUpdate(String usuario, String campo, String valor, long fecha) {
        this.usuario = usuario;
        this.campo = campo;
        this.valor = valor;
        this.fecha = fecha;
    }

    public int getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getCampo() { return campo; }
    public String getValor() { return valor; }
    public long getFecha() { return fecha; }
}