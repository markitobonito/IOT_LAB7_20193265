package com.example.appplication.services;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Clase CloudStorage encargada de gestionar la carga y descarga de archivos
 * a Firebase Cloud Storage
 * Pregunta 2: Firebase Storage
 */
public class CloudStorage {
    private static final String TAG = "CloudStorage";
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String userId;

    // Interfaz para callbacks de Storage
    public interface StorageCallback {
        void onSuccess(String result);
        void onError(String errorMessage);
        void onProgress(double progressPercentage);
    }

    /**
     * Constructor de CloudStorage
     * @param userId ID del usuario para crear carpeta personalizada
     */
    public CloudStorage(String userId) {
        this.userId = userId;
        initializeStorage();
    }

    /**
     * Pregunta 2.1a: Conexión al servicio de Firebase Storage
     * Instancia los objetos necesarios para acceder a Cloud Storage
     */
    private void initializeStorage() {
        storage = FirebaseStorage.getInstance();
        // Crear referencia a la carpeta del usuario: /users/{userId}
        storageReference = storage.getReference("users").child(userId);
        Log.d(TAG, "Firebase Storage inicializado correctamente para usuario: " + userId);
    }

    /**
     * Pregunta 2.1b: Guardar archivo (imagen) a Storage
     * Sube una imagen desde el dispositivo a Firebase Cloud Storage
     *
     * @param fileUri URI del archivo a subir (desde galería o cámara)
     * @param fileName Nombre del archivo en storage
     * @param callback Callback para manejar progreso, éxito y error
     */
    public void uploadFile(Uri fileUri, String fileName, StorageCallback callback) {
        if (fileUri == null) {
            callback.onError("La URI del archivo no puede ser nula");
            return;
        }

        // Crear referencia al archivo con el nombre especificado
        StorageReference fileReference = storageReference.child(fileName);

        // Subir el archivo y monitorear el progreso
        fileReference.putFile(fileUri)
                .addOnProgressListener(snapshot -> {
                    // Calcular porcentaje de progreso
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    Log.d(TAG, "Progreso de carga: " + progress + "%");
                    callback.onProgress(progress);
                })
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, "Archivo subido exitosamente: " + fileName);
                    // Obtener la URL de descarga después de subir
                    getDownloadUrl(fileName, callback);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error subiendo archivo", e);
                    callback.onError("Error al subir archivo: " + e.getMessage());
                });
    }

    /**
     * Pregunta 2.1c: Obtener URL de descarga del archivo
     * Obtiene la URL pública para descargar/visualizar el archivo
     *
     * @param fileName Nombre del archivo en storage
     * @param callback Callback que recibe la URL de descarga
     */
    public void getDownloadUrl(String fileName, StorageCallback callback) {
        StorageReference fileReference = storageReference.child(fileName);

        fileReference.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    Log.d(TAG, "URL de descarga obtenida: " + downloadUrl);
                    callback.onSuccess(downloadUrl);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error obteniendo URL de descarga", e);
                    callback.onError("Error al obtener URL: " + e.getMessage());
                });
    }

    /**
     * Verifica si un archivo existe en storage
     * Util para validar si ya existe una imagen de perfil
     *
     * @param fileName Nombre del archivo
     * @param callback Callback que indica si existe o no
     */
    public void fileExists(String fileName, StorageCallback callback) {
        StorageReference fileReference = storageReference.child(fileName);

        fileReference.getMetadata()
                .addOnSuccessListener(metadata -> {
                    Log.d(TAG, "Archivo existe: " + fileName);
                    callback.onSuccess("existe");
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Archivo no existe: " + fileName);
                    callback.onError("no existe");
                });
    }

    /**
     * Elimina un archivo de storage
     * @param fileName Nombre del archivo a eliminar
     * @param callback Callback para manejar éxito o error
     */
    public void deleteFile(String fileName, StorageCallback callback) {
        StorageReference fileReference = storageReference.child(fileName);

        fileReference.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Archivo eliminado: " + fileName);
                    callback.onSuccess("Archivo eliminado correctamente");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error eliminando archivo", e);
                    callback.onError("Error al eliminar archivo: " + e.getMessage());
                });
    }

    /**
     * Obtiene la referencia de storage del usuario
     * @return StorageReference
     */
    public StorageReference getStorageReference() {
        return storageReference;
    }
}
