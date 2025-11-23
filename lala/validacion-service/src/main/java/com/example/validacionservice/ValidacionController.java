package com.example.validacionservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/validar")
public class ValidacionController {

    @GetMapping("/dni/{dni}")
    public ResponseEntity<Map<String, Object>> validarDni(@PathVariable("dni") String dni) {
        Map<String, Object> resp = new HashMap<>();
        boolean valido = dni != null && dni.matches("\\d{8}");
        resp.put("valido", valido);
        if (!valido) {
            resp.put("mensaje", "El DNI no tiene un formato valido (8 digitos)");
            return ResponseEntity.badRequest().body(resp);
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/correo/{correo}")
    public ResponseEntity<Map<String, Object>> validarCorreo(@PathVariable("correo") String correo) {
        Map<String, Object> resp = new HashMap<>();
        boolean valido = correo != null && correo.toLowerCase().endsWith("@pucp.edu.pe");
        resp.put("valido", valido);
        if (!valido) {
            resp.put("mensaje", "El correo debe terminar en @pucp.edu.pe");
            return ResponseEntity.badRequest().body(resp);
        }
        return ResponseEntity.ok(resp);
    }
}

