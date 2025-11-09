package com.example.appplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import com.example.appplication.utils.FirestoreIndexHelper;

import com.example.appplication.adapters.VehicleAdapter;
import com.example.appplication.models.Vehicle;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

public class VehiclesActivity extends AppCompatActivity implements VehicleAdapter.OnVehicleClickListener {
    private static final String TAG = "VehiclesActivity";
    private RecyclerView recyclerVehicles;
    private VehicleAdapter adapter;
    private TextView tvNoVehicles;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicles);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
    userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    android.util.Log.d(TAG, "Current userId=" + userId);
    // Small visual aid for debugging in the emulator
    Toast.makeText(this, "UID=" + userId, Toast.LENGTH_LONG).show();

        // Inicializar vistas
        recyclerVehicles = findViewById(R.id.recyclerVehicles);
        tvNoVehicles = findViewById(R.id.tvNoVehicles);
        FloatingActionButton fabAddVehicle = findViewById(R.id.fabAddVehicle);

        // Configurar RecyclerView
        recyclerVehicles.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VehicleAdapter(this, new ArrayList<>(), this);
        recyclerVehicles.setAdapter(adapter);

        // Cargar vehículos
        loadVehicles();

        // Configurar FAB
        fabAddVehicle.setOnClickListener(v -> showAddVehicleDialog());
    }

    private void loadVehicles() {
        android.util.Log.d(TAG, "Running vehicles query for userId=" + userId);
    db.collection("vehicles")
        .whereEqualTo("userId", userId)
        .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        android.util.Log.e(TAG, "Error loading vehicles", error);
                        if (error instanceof FirebaseFirestoreException && 
                            FirestoreIndexHelper.isIndexRequiredException((FirebaseFirestoreException)error)) {
                            FirestoreIndexHelper.showIndexRequiredDialog(this, (FirebaseFirestoreException)error);
                        } else {
                            Toast.makeText(this, "Error al cargar vehículos: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        return;
                    }

                    List<Vehicle> vehicles = new ArrayList<>();
                    int count = 0;
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            count++;
                            android.util.Log.d(TAG, "docId=" + doc.getId() + " data=" + doc.getData());
                            Vehicle vehicle = doc.toObject(Vehicle.class);
                            // ensure id is set if DocumentId didn't populate
                            if (vehicle.getId() == null) vehicle.setId(doc.getId());
                            vehicles.add(vehicle);
                        }
                    }

                    android.util.Log.d(TAG, "Query returned " + count + " vehicles for userId=" + userId);
                    adapter.updateVehicles(vehicles);
                    tvNoVehicles.setVisibility(vehicles.isEmpty() ? View.VISIBLE : View.GONE);

                    // Debug fallback: if no vehicles found, fetch all vehicles and log their userIds
                    if (vehicles.isEmpty()) {
                        android.util.Log.w(TAG, "No vehicles for userId, fetching all vehicles for debugging...");
                        db.collection("vehicles").get()
                                .addOnSuccessListener(querySnapshot -> {
                                    android.util.Log.d(TAG, "Total vehicles in collection: " + querySnapshot.size());
                                    for (QueryDocumentSnapshot d : querySnapshot) {
                                        Map<String, Object> data = d.getData();
                                        android.util.Log.d(TAG, "ALL docId=" + d.getId() + " userId=" + data.get("userId") + " data=" + data);
                                    }
                                })
                                .addOnFailureListener(e -> android.util.Log.e(TAG, "Failed to fetch all vehicles: ", e));
                    }
                });
    }

    private void showAddVehicleDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_vehicle, null);
        
        // Referencias a las vistas del diálogo
        TextInputEditText etNickname = dialogView.findViewById(R.id.etNickname);
        TextInputEditText etLicensePlate = dialogView.findViewById(R.id.etLicensePlate);
        TextInputEditText etBrand = dialogView.findViewById(R.id.etBrand);
        TextInputEditText etModel = dialogView.findViewById(R.id.etModel);
        TextInputEditText etYear = dialogView.findViewById(R.id.etYear);
        Button btnSelectDate = dialogView.findViewById(R.id.btnSelectDate);

        final Timestamp[] selectedDate = {new Timestamp(new Date())};

        btnSelectDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar selected = Calendar.getInstance();
                        selected.set(year, month, dayOfMonth);
                        selectedDate[0] = new Timestamp(selected.getTime());
                        btnSelectDate.setText("Última revisión: " + 
                            new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "PE"))
                                .format(selected.getTime()));
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        new MaterialAlertDialogBuilder(this)
                .setTitle("Agregar vehículo")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    // Validar campos
                    if (etNickname.getText().toString().trim().isEmpty() ||
                            etLicensePlate.getText().toString().trim().isEmpty() ||
                            etBrand.getText().toString().trim().isEmpty() ||
                            etModel.getText().toString().trim().isEmpty() ||
                            etYear.getText().toString().trim().isEmpty()) {
                        Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Crear vehículo
                    Vehicle vehicle = new Vehicle(
                            etNickname.getText().toString().trim(),
                            etLicensePlate.getText().toString().trim().toUpperCase(),
                            etBrand.getText().toString().trim(),
                            etModel.getText().toString().trim(),
                            Integer.parseInt(etYear.getText().toString().trim()),
                            selectedDate[0],
                            userId
                    );

                    // Guardar en Firestore
                    db.collection("vehicles")
                            .add(vehicle)
                            .addOnSuccessListener(documentReference -> 
                                Toast.makeText(this, "Vehículo agregado", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> 
                                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onVehicleClick(Vehicle vehicle) {
        // TODO: Implementar vista de detalles/edición del vehículo
    }

    @Override
    public void onVehicleLongClick(Vehicle vehicle, View view) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar vehículo")
                .setMessage("¿Está seguro que desea eliminar el vehículo " + vehicle.getNickname() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    db.collection("vehicles").document(vehicle.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Vehículo eliminado", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}