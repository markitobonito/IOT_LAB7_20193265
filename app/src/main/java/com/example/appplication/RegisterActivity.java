package com.example.appplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appplication.services.AuthService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * RegisterActivity - Pregunta 1: Registro de usuarios con validación de microservicio
 * Permite al usuario registrarse con: Nombre, DNI, Email y Contraseña
 * Valida DNI y correo a través del microservicio antes de crear la cuenta
 */
public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private TextInputEditText etName, etDNI, etEmail, etPassword, etConfirm;
    private MaterialButton btnRegister, btnBackToLogin;
    private ProgressBar progressBar;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);

        // Inicializar vistas
        initializeViews();

        // Inicializar servicio de autenticación
        authService = new AuthService(this);

        // Configurar listeners de botones
        setupButtonListeners();
    }

    private void initializeViews() {
        etName = findViewById(R.id.etName);
        etDNI = findViewById(R.id.etDNI);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupButtonListeners() {
        btnRegister.setOnClickListener(v -> performRegister());
        btnBackToLogin.setOnClickListener(v -> {
            finish();
        });
    }

    /**
     * Valida los campos y llama a AuthService.registerUser()
     * que a su vez valida con el microservicio antes de crear el usuario
     */
    private void performRegister() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String dni = etDNI.getText() != null ? etDNI.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String confirm = etConfirm.getText() != null ? etConfirm.getText().toString() : "";

        // Validaciones básicas en cliente
        if (name.isEmpty()) {
            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dni.isEmpty() || dni.length() != 8) {
            Toast.makeText(this, "El DNI debe tener exactamente 8 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "El email no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar progreso
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        // Llamar a AuthService.registerUser que:
        // 1. Envía DNI + email al microservicio POST /registro
        // 2. Si es exitoso, crea usuario en Firebase
        // 3. Guarda datos en Firestore
        authService.registerUser(email, password, name, dni, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Registro exitoso: " + message, Toast.LENGTH_LONG).show();
                
                // Ir a MainActivity después de registrarse
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Error en registro: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
