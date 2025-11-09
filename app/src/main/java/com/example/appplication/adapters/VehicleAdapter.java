package com.example.appplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appplication.R;
import com.example.appplication.models.Vehicle;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {
    private List<Vehicle> vehicles;
    private Context context;
    private OnVehicleClickListener listener;
    private SimpleDateFormat dateFormat;

    public interface OnVehicleClickListener {
        void onVehicleClick(Vehicle vehicle);
        void onVehicleLongClick(Vehicle vehicle, View view);
    }

    public VehicleAdapter(Context context, List<Vehicle> vehicles, OnVehicleClickListener listener) {
        this.context = context;
        this.vehicles = vehicles;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "PE"));
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vehicle, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicles.get(position);
        holder.bind(vehicle);
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    public void updateVehicles(List<Vehicle> newVehicles) {
        vehicles.clear();
        vehicles.addAll(newVehicles);
        notifyDataSetChanged();
    }

    class VehicleViewHolder extends RecyclerView.ViewHolder {
        TextView tvNickname, tvLicensePlate, tvBrandModel, tvLastReview;

        VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNickname = itemView.findViewById(R.id.tvNickname);
            tvLicensePlate = itemView.findViewById(R.id.tvLicensePlate);
            tvBrandModel = itemView.findViewById(R.id.tvBrandModel);
            tvLastReview = itemView.findViewById(R.id.tvLastReview);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onVehicleClick(vehicles.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onVehicleLongClick(vehicles.get(position), v);
                    return true;
                }
                return false;
            });
        }

        void bind(Vehicle vehicle) {
            tvNickname.setText(vehicle.getNickname());
            tvLicensePlate.setText(vehicle.getLicensePlate());
            tvBrandModel.setText(String.format("%s %s (%d)", vehicle.getBrand(), vehicle.getModel(), vehicle.getYear()));
            if (vehicle.getLastTechnicalReview() != null) {
                tvLastReview.setText(String.format("Última revisión: %s", 
                    dateFormat.format(vehicle.getLastTechnicalReview().toDate())));
            }
        }
    }
}