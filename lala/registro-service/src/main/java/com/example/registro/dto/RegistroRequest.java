package com.example.registro.dto;

public class RegistroRequest {
    private String email;
    private String dni;

    public RegistroRequest() {}

    public RegistroRequest(String email, String dni) {
        this.email = email;
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }
}

