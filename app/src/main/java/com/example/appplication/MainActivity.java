package com.example.appplication;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Referencias a las vistas
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        Button btnVehicles = findViewById(R.id.btnVehicles);
        Button btnRecords = findViewById(R.id.btnRecords);
        Button btnSummary = findViewById(R.id.btnSummary);
        Button btnProfile = findViewById(R.id.btnProfile);
        Button btnSignOut = findViewById(R.id.btnSignOut);

        // Verificar si el usuario está autenticado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Redirigir a la pantalla de login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Mostrar nombre o correo en la UI
        String name = currentUser.getDisplayName();
        if (name == null || name.isEmpty()) name = currentUser.getEmail();
        tvWelcome.setText("Bienvenido, " + (name != null ? name : "usuario"));

        // Configurar navegación
        btnVehicles.setOnClickListener(v -> {
            startActivity(new Intent(this, VehiclesActivity.class));
        });

        btnRecords.setOnClickListener(v -> {
            startActivity(new Intent(this, RecordsActivity.class));
        });

        btnSummary.setOnClickListener(v -> {
            // TODO: Implementar SummaryActivity
            Toast.makeText(this, "Próximamente", Toast.LENGTH_SHORT).show();
        });

        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                startActivity(new Intent(this, ProfileActivity.class));
            });
        }

        if (btnSignOut != null) {
            btnSignOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AuthUI.getInstance()
                            .signOut(MainActivity.this)
                            .addOnCompleteListener(task -> {
                                Log.d(TAG, "logout exitoso");
                                // Volver al login
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();
                            });
                }
            });
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}