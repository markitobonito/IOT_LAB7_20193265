package com.example.appplication.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class FirestoreIndexHelper {
    public static boolean isIndexRequiredException(FirebaseFirestoreException e) {
        return e != null && e.getCode() == FirebaseFirestoreException.Code.FAILED_PRECONDITION 
               && e.getMessage() != null && e.getMessage().contains("requires an index");
    }

    public static String extractIndexLink(FirebaseFirestoreException e) {
        if (e == null || e.getMessage() == null) return null;
        
        String message = e.getMessage();
        int start = message.indexOf("https://console.firebase.google.com");
        if (start == -1) return null;
        
        return message.substring(start).trim();
    }

    public static void showIndexRequiredDialog(Activity activity, FirebaseFirestoreException e) {
        String indexLink = extractIndexLink(e);
        if (indexLink == null) return;

        new MaterialAlertDialogBuilder(activity)
            .setTitle("Índice requerido")
            .setMessage("Esta consulta requiere un índice en Firestore. ¿Desea crear el índice ahora?")
            .setPositiveButton("Crear índice", (dialog, which) -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(indexLink));
                activity.startActivity(browserIntent);
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }
}