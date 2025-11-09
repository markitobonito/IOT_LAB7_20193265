package com.example.appplication.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

public class FuelRecord {
    @DocumentId
    private String id;
    private String vehicleId;
    private Timestamp date;
    private double liters;
    private double mileage;
    private double totalPrice;
    private String fuelType;
    private String userId;

    // Constructor vacío requerido para Firestore
    public FuelRecord() {}

    public FuelRecord(String vehicleId, Timestamp date, double liters, double mileage, 
                     double totalPrice, String fuelType, String userId) {
        this.vehicleId = vehicleId;
        this.date = date;
        this.liters = liters;
        this.mileage = mileage;
        this.totalPrice = totalPrice;
        this.fuelType = fuelType;
        this.userId = userId;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public double getLiters() {
        return liters;
    }

    public void setLiters(double liters) {
        this.liters = liters;
    }

    public double getMileage() {
        return mileage;
    }

    public void setMileage(double mileage) {
        this.mileage = mileage;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Tipos de combustible disponibles
    public static final String FUEL_TYPE_GASOLINE = "Gasolina";
    public static final String FUEL_TYPE_LPG = "GLP";
    public static final String FUEL_TYPE_NGV = "GNV";

    public static String[] getFuelTypes() {
        return new String[]{FUEL_TYPE_GASOLINE, FUEL_TYPE_LPG, FUEL_TYPE_NGV};
    }
}