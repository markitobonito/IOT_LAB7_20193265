package com.example.appplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appplication.adapters.FuelRecordAdapter;
import com.example.appplication.models.FuelRecord;
import com.example.appplication.models.Vehicle;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.example.appplication.utils.FirestoreIndexHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecordsActivity extends AppCompatActivity implements FuelRecordAdapter.OnFuelRecordClickListener {
    private RecyclerView recyclerRecords;
    private FuelRecordAdapter adapter;
    private TextView tvNoRecords;
    private FirebaseFirestore db;
    private String userId;
    private SimpleDateFormat dateFormat;
    private Calendar startDate, endDate;
    private Map<String, Vehicle> vehiclesMap;
    private String selectedVehicleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "PE"));

        // Inicializar vistas
        recyclerRecords = findViewById(R.id.recyclerRecords);
        tvNoRecords = findViewById(R.id.tvNoRecords);
        FloatingActionButton fabAddRecord = findViewById(R.id.fabAddRecord);
        Button btnStartDate = findViewById(R.id.btnStartDate);
        Button btnEndDate = findViewById(R.id.btnEndDate);
        AutoCompleteTextView dropdownVehicle = findViewById(R.id.dropdownVehicle);

        // Inicializar fechas
        startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);
        endDate = Calendar.getInstance();

        btnStartDate.setText("Desde: " + dateFormat.format(startDate.getTime()));
        btnEndDate.setText("Hasta: " + dateFormat.format(endDate.getTime()));

        // Configurar RecyclerView
        recyclerRecords.setLayoutManager(new LinearLayoutManager(this));
        vehiclesMap = new HashMap<>();
        adapter = new FuelRecordAdapter(this, new ArrayList<>(), vehiclesMap, this);
        recyclerRecords.setAdapter(adapter);

        // Cargar vehículos para el filtro
        loadVehicles(dropdownVehicle);

        // Configurar listeners
        fabAddRecord.setOnClickListener(v -> showAddRecordDialog());

        btnStartDate.setOnClickListener(v -> showDatePicker(startDate, btnStartDate, "Desde: "));
        btnEndDate.setOnClickListener(v -> showDatePicker(endDate, btnEndDate, "Hasta: "));

        dropdownVehicle.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = parent.getItemAtPosition(position).toString();
            for (Vehicle vehicle : vehiclesMap.values()) {
                if (vehicle.getNickname().equals(selectedItem)) {
                    selectedVehicleId = vehicle.getId();
                    loadRecords();
                    break;
                }
            }
        });

        // Cargar registros iniciales
        loadRecords();
    }

    private void loadVehicles(AutoCompleteTextView spinner) {
        db.collection("vehicles")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> vehicleNames = new ArrayList<>();
                    vehicleNames.add("Todos los vehículos");

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Vehicle vehicle = document.toObject(Vehicle.class);
                        vehiclesMap.put(vehicle.getId(), vehicle);
                        vehicleNames.add(vehicle.getNickname());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this, android.R.layout.simple_dropdown_item_1line, vehicleNames);
                    spinner.setAdapter(adapter);
                    spinner.setText("Todos los vehículos", false);
                })
                .addOnFailureListener(e -> 
                    Toast.makeText(this, "Error al cargar vehículos", Toast.LENGTH_SHORT).show());
    }

    private void loadRecords() {
        Query query = db.collection("fuelRecords")
                .whereEqualTo("userId", userId);

        // Aplicar filtro de fecha
        if (startDate != null && endDate != null) {
            query = query.whereGreaterThanOrEqualTo("date", new Timestamp(startDate.getTime()))
                        .whereLessThanOrEqualTo("date", new Timestamp(endDate.getTime()));
        }

        // Aplicar filtro de vehículo
        if (selectedVehicleId != null && !selectedVehicleId.isEmpty()) {
            query = query.whereEqualTo("vehicleId", selectedVehicleId);
        }

        if (selectedVehicleId != null) {
            query = query.whereEqualTo("vehicleId", selectedVehicleId);
        }

        query.addSnapshotListener((value, error) -> {
            if (error != null) {
                // Mostrar mensaje detallado para facilitar la creación del índice desde la consola
                android.util.Log.e("RecordsActivity", "Error en snapshot listener", error);
                String errMsg = error.getMessage() != null ? error.getMessage() : "Error al cargar registros";
                Toast.makeText(this, "Error al cargar registros: " + errMsg, Toast.LENGTH_LONG).show();
                return;
            }

            List<FuelRecord> records = new ArrayList<>();
            if (value != null) {
                android.util.Log.d("RecordsActivity", "Snapshot no es null, size=" + value.size());
                for (QueryDocumentSnapshot doc : value) {
                    android.util.Log.d("RecordsActivity", "Documento encontrado: " + doc.getId() + " data=" + doc.getData());
                    FuelRecord record = doc.toObject(FuelRecord.class);
                    records.add(record);
                }
            } else {
                android.util.Log.d("RecordsActivity", "Snapshot es null");
            }

            android.util.Log.d("RecordsActivity", "Total registros encontrados: " + records.size());
            // Actualizar la UI
            adapter.updateRecords(records);
            // Mostrar/ocultar el mensaje de "No hay registros"
            tvNoRecords.setVisibility(records.isEmpty() ? View.VISIBLE : View.GONE);
            tvNoRecords.setVisibility(records.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void showDatePicker(Calendar date, Button button, String prefix) {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    date.set(year, month, dayOfMonth);
                    button.setText(prefix + dateFormat.format(date.getTime()));
                    loadRecords();
                },
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void showAddRecordDialog() {
        if (vehiclesMap.isEmpty()) {
            Toast.makeText(this, "Primero debe agregar un vehículo", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_fuel_record, null);
        AutoCompleteTextView spinnerVehicle = dialogView.findViewById(R.id.spinnerDialogVehicle);
        AutoCompleteTextView spinnerFuelType = dialogView.findViewById(R.id.spinnerFuelType);
        Button btnSelectDate = dialogView.findViewById(R.id.btnSelectDate);
        TextInputEditText etLiters = dialogView.findViewById(R.id.etLiters);
        TextInputEditText etMileage = dialogView.findViewById(R.id.etMileage);
        TextInputEditText etTotalPrice = dialogView.findViewById(R.id.etTotalPrice);

        // Configurar spinner de vehículos
        List<String> vehicleNames = new ArrayList<>();
        Map<String, String> vehicleIdsByName = new HashMap<>();
        for (Vehicle vehicle : vehiclesMap.values()) {
            vehicleNames.add(vehicle.getNickname());
            vehicleIdsByName.put(vehicle.getNickname(), vehicle.getId());
        }
        ArrayAdapter<String> vehiclesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, vehicleNames);
        spinnerVehicle.setAdapter(vehiclesAdapter);

        // Configurar spinner de tipos de combustible
        ArrayAdapter<String> fuelTypesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, FuelRecord.getFuelTypes());
        spinnerFuelType.setAdapter(fuelTypesAdapter);

        // Fecha por defecto
        final Calendar[] selectedDate = {Calendar.getInstance()};
        btnSelectDate.setText("Fecha: " + dateFormat.format(selectedDate[0].getTime()));
        btnSelectDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate[0].set(year, month, dayOfMonth);
                        btnSelectDate.setText("Fecha: " + dateFormat.format(selectedDate[0].getTime()));
                    },
                    selectedDate[0].get(Calendar.YEAR),
                    selectedDate[0].get(Calendar.MONTH),
                    selectedDate[0].get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });

        new MaterialAlertDialogBuilder(this)
                .setTitle("Agregar registro")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    // Validar campos
                    String vehicleName = spinnerVehicle.getText().toString();
                    String vehicleId = vehicleIdsByName.get(vehicleName);
                    String fuelType = spinnerFuelType.getText().toString();
                    String litersStr = etLiters.getText().toString();
                    String mileageStr = etMileage.getText().toString();
                    String priceStr = etTotalPrice.getText().toString();

                    if (vehicleId == null || fuelType.isEmpty() || litersStr.isEmpty() || 
                        mileageStr.isEmpty() || priceStr.isEmpty()) {
                        Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double mileage = Double.parseDouble(mileageStr);

                    // Verificar último kilometraje del vehículo
                    db.collection("fuelRecords")
                            .whereEqualTo("vehicleId", vehicleId)
                            .orderBy("mileage", Query.Direction.DESCENDING)
                            .limit(1)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                Double lastMileage = null;
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    lastMileage = queryDocumentSnapshots.getDocuments().get(0)
                                            .toObject(FuelRecord.class).getMileage();
                                }

                                    if (lastMileage != null && mileage <= lastMileage) {
                                        Toast.makeText(this, 
                                            "El kilometraje debe ser mayor que el último registro: " + 
                                            String.format("%.1f km", lastMileage), 
                                            Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    // Crear y guardar registro
                                    FuelRecord record = new FuelRecord(
                                            vehicleId,
                                            new Timestamp(selectedDate[0].getTime()),
                                            Double.parseDouble(litersStr),
                                            mileage,
                                            Double.parseDouble(priceStr),
                                            fuelType,
                                            userId
                                    );

                                    db.collection("fuelRecords")
                                            .add(record)
                                            .addOnSuccessListener(documentReference -> 
                                                Toast.makeText(this, "Registro agregado", Toast.LENGTH_SHORT).show())
                                            .addOnFailureListener(e -> {
                                                android.util.Log.e("RecordsActivity", "Error al guardar registro", e);
                                                Toast.makeText(this, "Error al guardar: " + (e.getMessage() != null ? e.getMessage() : ""), Toast.LENGTH_LONG).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    // Esto falla si la consulta requiere un índice compuesto (FAILED_PRECONDITION)
                                    android.util.Log.e("RecordsActivity", "Error al consultar último kilometraje", e);
                                    String msg = e.getMessage() != null ? e.getMessage() : "Error al consultar último kilometraje";
                                    Toast.makeText(this, "No se pudo verificar último kilometraje: " + msg, Toast.LENGTH_LONG).show();
                                });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onRecordClick(FuelRecord record) {
        // TODO: Implementar edición de registro
    }

    @Override
    public void onRecordLongClick(FuelRecord record, View view) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar registro")
                .setMessage("¿Está seguro que desea eliminar este registro?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    db.collection("fuelRecords").document(record.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> 
                                Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> 
                                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}