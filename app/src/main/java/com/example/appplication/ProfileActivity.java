package com.example.appplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.appplication.services.AuthService;
import com.example.appplication.services.CloudStorage;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * ProfileActivity - Pregunta 2: Firebase Storage
 * Permite al usuario ver y actualizar su perfil
 * - Mostrar datos del usuario (nombre, email, DNI)
 * - Subir una imagen de perfil
 * - Visualizar la imagen de perfil
 * - Obtener y mostrar la URL de almacenamiento
 */
public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String PROFILE_IMAGE_FILENAME = "profile_image.jpg";

    private ImageView ivProfileImage;
    private TextView tvName, tvEmail, tvDNI, tvImageUrl;
    private MaterialButton btnUploadImage, btnBack;
    private ProgressBar progressBar;
    private Uri imageUri;

    private AuthService authService;
    private CloudStorage cloudStorage;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inicializar vistas
        initializeViews();

        // Inicializar servicios
        authService = new AuthService(this);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            cloudStorage = new CloudStorage(currentUser.getUid());
            db = FirebaseFirestore.getInstance();

            // Cargar datos del usuario
            loadUserData();

            // Cargar imagen de perfil si existe
            loadProfileImage();
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Configurar listeners de botones
        setupButtonListeners();
    }

    private void initializeViews() {
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvDNI = findViewById(R.id.tvDNI);
        tvImageUrl = findViewById(R.id.tvImageUrl);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupButtonListeners() {
        btnUploadImage.setOnClickListener(v -> openImagePicker());
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    /**
     * Carga los datos del usuario desde Firestore
     */
    private void loadUserData() {
        db.collection("users").document(currentUser.getUid())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(ProfileActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null && value.exists()) {
                        String name = value.getString("name");
                        String email = value.getString("email");
                        String dni = value.getString("dni");

                        tvName.setText("Nombre: " + (name != null ? name : "N/A"));
                        tvEmail.setText("Email: " + (email != null ? email : "N/A"));
                        tvDNI.setText("DNI: " + (dni != null ? dni : "N/A"));
                    }
                });
    }

    /**
     * Carga la imagen de perfil del usuario desde Cloud Storage
     */
    private void loadProfileImage() {
        // La imagen se carga desde Storage cuando el usuario la sube
        // No se carga automáticamente de Firestore
    }

    /**
     * Carga una imagen usando Glide desde una URL de Firebase Storage
     * @param imageUrl URL completa de la imagen
     */
    private void loadImageWithGlide(String imageUrl) {
        try {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivProfileImage);
        } catch (Exception e) {
            Log.e(TAG, "Error loading image with Glide", e);
        }
    }

    /**
     * Abre el selector de imágenes del dispositivo
     */
    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadProfileImage();
        }
    }

    /**
     * Sube la imagen de perfil a Firebase Cloud Storage
     */
    private void uploadProfileImage() {
        if (imageUri == null) {
            Toast.makeText(this, "No se seleccionó una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnUploadImage.setEnabled(false);

        cloudStorage.uploadFile(imageUri, PROFILE_IMAGE_FILENAME, new CloudStorage.StorageCallback() {
            @Override
            public void onSuccess(String result) {
                // result es la URL de descarga
                progressBar.setVisibility(View.GONE);
                btnUploadImage.setEnabled(true);

                // Mostrar toast con la URL
                Toast.makeText(ProfileActivity.this, 
                    "Imagen subida exitosamente\nURL: " + result, 
                    Toast.LENGTH_LONG).show();

                // Cargar la imagen en el ImageView
                loadImageWithGlide(result);

                // Mostrar URL en la vista
                tvImageUrl.setText("URL: " + result);
                tvImageUrl.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                btnUploadImage.setEnabled(true);
                Toast.makeText(ProfileActivity.this, 
                    "Error: " + errorMessage, 
                    Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(double progressPercentage) {
                progressBar.setProgress((int) progressPercentage);
            }
        });
    }

}
