package com.example.appplication.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

public class Vehicle {
    @DocumentId
    private String id;
    private String nickname;
    private String licensePlate;
    private String brand;
    private String model;
    private int year;
    private Timestamp lastTechnicalReview;
    private String userId;

    // Constructor vacío requerido para Firestore
    public Vehicle() {}

    public Vehicle(String nickname, String licensePlate, String brand, String model, int year, Timestamp lastTechnicalReview, String userId) {
        this.nickname = nickname;
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.lastTechnicalReview = lastTechnicalReview;
        this.userId = userId;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Timestamp getLastTechnicalReview() {
        return lastTechnicalReview;
    }

    public void setLastTechnicalReview(Timestamp lastTechnicalReview) {
        this.lastTechnicalReview = lastTechnicalReview;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}