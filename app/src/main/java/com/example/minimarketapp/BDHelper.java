package com.example.minimarketapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class BDHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "ventas.db";
    private static final int DB_VERSION = 3;
<<<<<<< HEAD
=======
    private Context context; // Agregamos el contexto para acceder a SharedPreferences
>>>>>>> 7752156 (creación del perfil usuario con imagen de perfil y opción de cambiar contraseña y botón de cerrar sesión)

    public BDHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context; // Guardamos el contexto que se pasa al constructor
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla usuarios
        db.execSQL("CREATE TABLE usuarios(id INTEGER PRIMARY KEY AUTOINCREMENT, usuario TEXT, correo TEXT, password TEXT)");

        // Crear tabla productos
        db.execSQL("CREATE TABLE productos(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, precio REAL, imagen TEXT)");

        db.execSQL("CREATE TABLE pedidos(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "cliente TEXT, " +
                "direccion TEXT, " +
                "productos TEXT, " +
                "estado TEXT, " +
                "fecha INTEGER)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS productos");
        db.execSQL("DROP TABLE IF EXISTS pedidos");
        onCreate(db);
    }

    // Métodos para usuarios
    public long insertarUsuario(String usuario, String correo, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("usuario", usuario);
        cv.put("correo", correo);
        cv.put("password", password);
        return db.insert("usuarios", null, cv);
    }


    // Verificar si la contraseña es correcta
    public boolean verificarPassword(String passwordActual) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = getUserId(); // ← obtiene el ID del usuario en sesión
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE id = ? AND password = ?", new String[]{String.valueOf(userId), passwordActual});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }


    // Actualizar contraseña
    public boolean actualizarPassword(String nuevaPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("password", nuevaPassword);
        int result = db.update("usuarios", contentValues, "id = ?", new String[]{String.valueOf(getUserId())});
        return result > 0;
    }

    // Método de login
    public boolean login(String usuario, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE usuario = ? AND password = ?", new String[]{usuario, password});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    // Obtener el correo del usuario
    public String obtenerCorreo(String usuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT correo FROM usuarios WHERE usuario = ?", new String[]{usuario});

        if (cursor != null && cursor.moveToFirst()) {
            String correo = cursor.getString(0);
            cursor.close();
            return correo;
        }
        return null; // Si no se encuentra, devuelve null
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

    public boolean agregarPedido(String cliente, String direccion, String productos, String estado, long fecha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("cliente", cliente);
        cv.put("direccion", direccion);
        cv.put("productos", productos);
        cv.put("estado", estado);
        cv.put("fecha", fecha);
        long result = db.insert("pedidos", null, cv);
        return result != -1;
    }

    public Cursor obtenerPedidos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM pedidos", null);
    }

    public boolean actualizarPedidoEstado(int id, String nuevoEstado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("estado", nuevoEstado);
        int filas = db.update("pedidos", cv, "id = ?", new String[]{String.valueOf(id)});
        return filas > 0;
    }

    public boolean eliminarPedido(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filas = db.delete("pedidos", "id = ?", new String[]{String.valueOf(id)});
        return filas > 0;
    }

    // Método para obtener el ID del usuario desde SharedPreferences
    private int getUserId() {
        SharedPreferences prefs = context.getSharedPreferences("MiPreferencia", Context.MODE_PRIVATE);
        return prefs.getInt("user_id", -1);  // Devuelve -1 si no hay usuario
    }
}


