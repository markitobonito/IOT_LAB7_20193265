// ...existing code...
package com.example.registro.dto;

public class ValidacionResponse {
    private boolean valido;
    private String mensaje;

    public ValidacionResponse() {}

    public ValidacionResponse(boolean valido, String mensaje) {
        this.valido = valido;
        this.mensaje = mensaje;
    }

    public boolean isValido() {
        return valido;
    }

    public void setValido(boolean valido) {
        this.valido = valido;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}

