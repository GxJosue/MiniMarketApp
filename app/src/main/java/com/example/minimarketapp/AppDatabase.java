package com.example.minimarketapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Usuario.class, Producto.class, Pedido.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UsuarioDao usuarioDao();
    public abstract ProductoDao productoDao();
    public abstract PedidoDao pedidoDao();

    private static AppDatabase INSTANCE;
    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "ventas.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}


