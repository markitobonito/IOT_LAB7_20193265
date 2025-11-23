package com.example.appplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appplication.services.AuthService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * LoginActivity - Pantalla de inicio de sesión
 * Permite al usuario iniciar sesión o acceder a la pantalla de registro
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnRegister, btnForgotPassword;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar vistas
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);

        // Inicializar AuthService
        authService = new AuthService(this);

        // Configurar listeners
        setupButtonListeners();

        // Si ya hay usuario autenticado, ir a MainActivity
        if (authService.isUserAuthenticated()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void setupButtonListeners() {
        btnLogin.setOnClickListener(v -> performLogin());
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
        btnForgotPassword.setOnClickListener(v -> performForgotPassword());
    }

    /**
     * Realiza el login con correo y contraseña
     */
    private void performLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        if (email.isEmpty()) {
            Toast.makeText(this, "El email no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        authService.signInWithEmail(email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Realiza la recuperación de contraseña
     */
    private void performForgotPassword() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";

        if (email.isEmpty()) {
            Toast.makeText(this, "Ingresa tu email para recuperar la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        btnForgotPassword.setEnabled(false);

        authService.resetPassword(email, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                btnForgotPassword.setEnabled(true);
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String errorMessage) {
                btnForgotPassword.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
