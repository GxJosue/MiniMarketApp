package com.example.minimarketapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncWorker extends Worker {

    private static final String TAG = "SyncWorker";

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Acceso a DAOs
            UsuarioDao usuarioDao = AppDatabase.getInstance(getApplicationContext()).usuarioDao();
            PendingUpdateDao pendingDao = AppDatabase.getInstance(getApplicationContext()).pendingUpdateDao();

            // Obtener todos los pendientes
            List<PendingUpdate> pendientes = pendingDao.obtenerTodos();
            if (pendientes == null || pendientes.isEmpty()) {
                Log.d(TAG, "No hay pendientes para sincronizar.");
                return Result.success();
            }

            Log.d(TAG, "Pendientes encontrados: " + pendientes.size());

            for (PendingUpdate p : pendientes) {
                try {
                    String email = p.usuario;
                    // Obtener credenciales locales
                    Usuario localUser = usuarioDao.obtenerPorUsuario(email);
                    if (localUser == null) {
                        Log.w(TAG, "No se encontró usuario local para email: " + email + ". Saltando pendiente id=" + p.getId());
                        continue;
                    }

                    // Si no hay usuario autenticado en Firebase, intentar sign-in con credenciales locales
                    FirebaseUser current = mAuth.getCurrentUser();
                    if (current == null) {
                        try {
                            Task<AuthResult> signInTask = mAuth.signInWithEmailAndPassword(email, localUser.password);
                            Tasks.await(signInTask); // bloquea hasta completar
                            if (!signInTask.isSuccessful()) {
                                Log.w(TAG, "Firebase signIn falló para " + email + ": " + (signInTask.getException() != null ? signInTask.getException().getMessage() : "unknown"));
                                // No podemos sincronizar este pendiente ahora
                                continue;
                            } else {
                                Log.d(TAG, "Firebase signIn exitoso para " + email);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Excepción en signIn await para " + email + ": " + e.getMessage(), e);
                            continue;
                        }
                    }

                    // Obtener uid actual (ya autenticado)
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser == null) {
                        Log.w(TAG, "currentUser aún es null después de signIn para " + email);
                        continue;
                    }
                    String uid = firebaseUser.getUid();

                    // Preparar la actualización
                    Map<String, Object> updates = new HashMap<>();
                    updates.put(p.campo, p.valor);

                    // Ejecutar update en Firestore y esperar su resultado
                    try {
                        Task<Void> updateTask = db.collection("users").document(uid).update(updates);
                        Tasks.await(updateTask);
                        if (updateTask.isSuccessful()) {
                            // Borrar pendiente localmente
                            pendingDao.eliminarPorId(p.getId());
                            Log.d(TAG, "Pendiente id=" + p.getId() + " sincronizado y eliminado localmente.");
                        } else {
                            Log.w(TAG, "Update en Firestore no fue exitoso para pendiente id=" + p.getId() + ": " + (updateTask.getException() != null ? updateTask.getException().getMessage() : "unknown"));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error al actualizar Firestore para pendiente id=" + p.getId() + ": " + e.getMessage(), e);
                        // No eliminar pendiente; se reintentará en la próxima ejecución
                    }

                } catch (Exception ex) {
                    Log.e(TAG, "Error procesando pendiente id=" + p.getId() + ": " + ex.getMessage(), ex);
                }
            }

            return Result.success();

        } catch (Exception e) {
            Log.e("SyncWorker", "doWork error general: " + e.getMessage(), e);
            return Result.retry();
        }
    }
}