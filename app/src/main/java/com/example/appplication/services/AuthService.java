package com.example.appplication.services;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase AuthService encargada de gestionar toda la autenticación
 * Implementa métodos para: inicialización, login, registro, recuperación de contraseña y logout
 * Integración con microservicio de validación para el registro
 */
public class AuthService {
    private static final String TAG = "AuthService";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Activity activity;

    // Interfaz para callbacks de autenticación
    public interface AuthCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
    }

    /**
     * Constructor de AuthService
     * @param activity Actividad necesaria para operaciones de contexto
     */
    public AuthService(Activity activity) {
        this.activity = activity;
        initializeAuth();
    }

    /**
     * Pregunta 1.1: Conexión e inicialización de Firebase Authentication
     * Instancia los objetos de autenticación y Firestore
     */
    public void initializeAuth() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Firebase Auth inicializado correctamente");
    }

    /**
     * Pregunta 1.2: Inicio de Sesión con correo y contraseña
     * @param email Correo del usuario
     * @param password Contraseña del usuario
     * @param callback Interfaz para manejar éxito o error
     */
    public void signInWithEmail(String email, String password, AuthCallback callback) {
        if (!validateEmail(email) || !validatePassword(password)) {
            callback.onError("Email o contraseña inválidos");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            callback.onSuccess("Sesión iniciada correctamente");
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            String errorMessage = getAuthErrorMessage(task.getException());
                            callback.onError(errorMessage);
                        }
                    }
                });
    }

    /**
     * Pregunta 1.3: Recuperación de contraseña mediante correo de restablecimiento
     * @param email Correo del usuario para enviar el enlace de restablecimiento
     * @param callback Interfaz para manejar éxito o error
     */
    public void resetPassword(String email, AuthCallback callback) {
        if (!validateEmail(email)) {
            callback.onError("Email inválido");
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "resetPassword:success");
                            callback.onSuccess("Correo de restablecimiento enviado a " + email);
                        } else {
                            Log.w(TAG, "resetPassword:failure", task.getException());
                            String errorMessage = getAuthErrorMessage(task.getException());
                            callback.onError(errorMessage);
                        }
                    }
                });
    }

    /**
     * Pregunta 1.4: Cierre de Sesión
     * @param callback Interfaz para manejar éxito o error
     */
    public void signOut(AuthCallback callback) {
        try {
            mAuth.signOut();
            Log.d(TAG, "signOut:success");
            callback.onSuccess("Sesión cerrada correctamente");
        } catch (Exception e) {
            Log.e(TAG, "signOut:error", e);
            callback.onError("Error al cerrar sesión: " + e.getMessage());
        }
    }

    /**
     * Pregunta 1.5: Registro de nuevos usuarios
     * Primero valida el DNI y correo con el microservicio en http://192.168.x.x:8080/registro
     * Solo si es exitoso, crea el usuario en Firebase Auth y guarda datos en Firestore
     *
     * @param email Correo del usuario
     * @param password Contraseña del usuario
     * @param name Nombre completo del usuario
     * @param dni DNI del usuario (8 dígitos)
     * @param callback Interfaz para manejar éxito o error
     */
    public void registerUser(String email, String password, String name, String dni, AuthCallback callback) {
        // Validaciones básicas
        if (!validateEmail(email)) {
            callback.onError("El correo no es válido");
            return;
        }
        if (!validatePassword(password)) {
            callback.onError("La contraseña debe tener al menos 6 caracteres");
            return;
        }
        if (name == null || name.trim().isEmpty()) {
            callback.onError("El nombre no puede estar vacío");
            return;
        }
        if (!validateDNI(dni)) {
            callback.onError("El DNI debe tener 8 dígitos");
            return;
        }

        // PASO 1: Validar con el microservicio antes de crear el usuario
        validateWithMicroservice(email, dni, new AuthCallback() {
            @Override
            public void onSuccess(String message) {
                // PASO 2: Si microservicio valida, crear usuario en Firebase Auth
                createUserInFirebase(email, password, name, dni, callback);
            }

            @Override
            public void onError(String errorMessage) {
                // Si el microservicio rechaza, no creamos el usuario
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Valida el DNI y correo con el microservicio
     * Llamada HTTP POST a http://192.168.x.x:8080/registro
     * @param email Correo del usuario
     * @param dni DNI del usuario
     * @param callback Callback para manejar respuesta
     */
    private void validateWithMicroservice(String email, String dni, AuthCallback callback) {
        // Usar RetrofitService para hacer la llamada al microservicio
        RetrofitService.getInstance().validateRegistration(email, dni, callback);
    }

    /**
     * Crea el usuario en Firebase Authentication y guarda datos adicionales en Firestore
     * @param email Correo del usuario
     * @param password Contraseña
     * @param name Nombre completo
     * @param dni DNI del usuario
     * @param callback Callback final
     */
    private void createUserInFirebase(String email, String password, String name, String dni, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                // Actualizar perfil con el nombre
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Guardar datos adicionales en Firestore
                                                    saveUserDataToFirestore(user.getUid(), email, name, dni, callback);
                                                } else {
                                                    callback.onError("Error al actualizar perfil: " + task.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            String errorMessage = getAuthErrorMessage(task.getException());
                            callback.onError(errorMessage);
                        }
                    }
                });
    }

    /**
     * Guarda los datos del usuario en Firestore
     * @param userId ID del usuario en Firebase Auth
     * @param email Correo del usuario
     * @param name Nombre del usuario
     * @param dni DNI del usuario
     * @param callback Callback final
     */
    private void saveUserDataToFirestore(String userId, String email, String name, String dni, AuthCallback callback) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("name", name);
        userData.put("dni", dni);
        userData.put("createdAt", com.google.firebase.Timestamp.now());
        userData.put("profileImageUrl", ""); // Campo vacío inicialmente

        db.collection("users").document(userId)
                .set(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User data saved to Firestore");
                            callback.onSuccess("Usuario registrado correctamente");
                        } else {
                            Log.e(TAG, "Error saving user data", task.getException());
                            callback.onError("Error al guardar datos del usuario: " + task.getException());
                        }
                    }
                });
    }

    /**
     * Obtiene el usuario actualmente autenticado
     * @return FirebaseUser o null si no hay usuario
     */
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    /**
     * Verifica si el usuario está autenticado
     * @return true si está autenticado
     */
    public boolean isUserAuthenticated() {
        return mAuth.getCurrentUser() != null;
    }

    /**
     * Obtiene el UID del usuario actual
     * @return UID del usuario o null
     */
    public String getCurrentUserId() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    // ==================== VALIDACIONES ====================

    private boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean validatePassword(String password) {
        return password != null && password.length() >= 6;
    }

    private boolean validateDNI(String dni) {
        // Valida que el DNI tenga exactamente 8 dígitos
        return dni != null && dni.matches("^\\d{8}$");
    }

    /**
     * Mapea las excepciones de Firebase a mensajes de error legibles
     * @param exception La excepción lanzada por Firebase
     * @return Mensaje de error en español
     */
    private String getAuthErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            return "La contraseña es muy débil. Use al menos 6 caracteres";
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return "El correo no es válido o la contraseña es incorrecta";
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            return "El correo ya está registrado";
        } else {
            return exception != null ? exception.getMessage() : "Error desconocido en autenticación";
        }
    }
}
