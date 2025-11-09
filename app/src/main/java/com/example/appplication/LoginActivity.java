package com.example.appplication;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Arrays;
import java.util.List;
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private ActivityResultLauncher<Intent> signInLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Registrar el launcher para FirebaseUI
        signInLauncher = registerForActivityResult(new FirebaseAuthUIActivityResultContract(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, "Firebase uid: " + (user != null ? user.getUid() : "null"));
                // Ir a MainActivity después de iniciar sesión
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Log.d(TAG, "Canceló el Log-in");
            }
        });
        findViewById(R.id.btnStartSignIn).setOnClickListener(v -> startSignIn());
    }
    private void startSignIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder()
                    .setSignInOptions(
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build()
                    )
                    .build()
        );

        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .setLogo(R.mipmap.ic_launcher)
                .setTheme(R.style.Theme_MyApplication)
                .build();

        signInLauncher.launch(intent);
    }
}
