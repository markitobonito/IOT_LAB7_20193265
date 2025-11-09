package com.example.appplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appplication.R;
import com.example.appplication.models.FuelRecord;
import com.example.appplication.models.Vehicle;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FuelRecordAdapter extends RecyclerView.Adapter<FuelRecordAdapter.FuelRecordViewHolder> {
    private List<FuelRecord> records;
    private Context context;
    private OnFuelRecordClickListener listener;
    private SimpleDateFormat dateFormat;
    private Map<String, Vehicle> vehiclesMap;

    public interface OnFuelRecordClickListener {
        void onRecordClick(FuelRecord record);
        void onRecordLongClick(FuelRecord record, View view);
    }

    public FuelRecordAdapter(Context context, List<FuelRecord> records, 
                           Map<String, Vehicle> vehiclesMap, OnFuelRecordClickListener listener) {
        this.context = context;
        this.records = records;
        this.listener = listener;
        this.vehiclesMap = vehiclesMap;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "PE"));
    }

    @NonNull
    @Override
    public FuelRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fuel_record, parent, false);
        return new FuelRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FuelRecordViewHolder holder, int position) {
        FuelRecord record = records.get(position);
        holder.bind(record);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void updateRecords(List<FuelRecord> newRecords) {
        records.clear();
        records.addAll(newRecords);
        notifyDataSetChanged();
    }

    public void updateVehiclesMap(Map<String, Vehicle> newVehiclesMap) {
        vehiclesMap.clear();
        vehiclesMap.putAll(newVehiclesMap);
        notifyDataSetChanged();
    }

    class FuelRecordViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvVehicle, tvLiters, tvMileage, tvPrice, tvFuelType;

        FuelRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvVehicle = itemView.findViewById(R.id.tvVehicle);
            tvLiters = itemView.findViewById(R.id.tvLiters);
            tvMileage = itemView.findViewById(R.id.tvMileage);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvFuelType = itemView.findViewById(R.id.tvFuelType);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onRecordClick(records.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onRecordLongClick(records.get(position), v);
                    return true;
                }
                return false;
            });
        }

        void bind(FuelRecord record) {
            tvDate.setText(dateFormat.format(record.getDate().toDate()));
            
            Vehicle vehicle = vehiclesMap.get(record.getVehicleId());
            tvVehicle.setText(vehicle != null ? vehicle.getNickname() : "Vehículo desconocido");
            
            tvLiters.setText(String.format("%.2f L", record.getLiters()));
            tvMileage.setText(String.format("%.1f km", record.getMileage()));
            tvPrice.setText(String.format("S/ %.2f", record.getTotalPrice()));
            tvFuelType.setText(record.getFuelType());
        }
    }
}