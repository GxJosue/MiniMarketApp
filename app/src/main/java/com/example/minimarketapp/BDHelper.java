package com.example.minimarketapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class BDHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "ventas.db";
    private static final int DB_VERSION = 2;

    public BDHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla usuarios
        db.execSQL("CREATE TABLE usuarios(id INTEGER PRIMARY KEY AUTOINCREMENT, usuario TEXT, password TEXT)");

        // Crear tabla productos
        db.execSQL("CREATE TABLE productos(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, precio REAL, imagen TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS productos");
        onCreate(db);
    }

    // Métodos para usuarios
    public boolean registrarUsuario(String usuario, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("usuario", usuario);
        cv.put("password", password);
        long result = db.insert("usuarios", null, cv);
        return result != -1;
    }

    public boolean login(String usuario, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE usuario = ? AND password = ?", new String[]{usuario, password});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    // CRUD para productos

    public boolean agregarProducto(String nombre, double precio, String imagen) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nombre", nombre);
        cv.put("precio", precio);
        cv.put("imagen", imagen);
        long result = db.insert("productos", null, cv);
        return result != -1;
    }

    public Cursor obtenerProductos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM productos", null);
    }

    public boolean actualizarProducto(int id, String nombre, double precio, String imagen) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nombre", nombre);
        cv.put("precio", precio);
        cv.put("imagen", imagen);
        int filas = db.update("productos", cv, "id = ?", new String[]{String.valueOf(id)});
        return filas > 0;
    }

    public boolean eliminarProducto(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filas = db.delete("productos", "id = ?", new String[]{String.valueOf(id)});
        return filas > 0;
    }

    public void insertarProductosPrueba() {
        if(obtenerProductos().getCount() == 0) {
            agregarProducto("Producto 1", 10.99, "");
            agregarProducto("Producto 2", 20.49, "");
            agregarProducto("Producto 3", 5.75, "");
        }
    }

}
