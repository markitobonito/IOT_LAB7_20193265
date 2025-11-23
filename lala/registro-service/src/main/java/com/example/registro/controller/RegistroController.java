package com.example.registro.controller;

import com.example.registro.dto.RegistroRequest;
import com.example.registro.dto.ValidacionResponse;
import com.example.registro.feign.ValidacionClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registro")
public class RegistroController {

    private final ValidacionClient validacionClient;
    private final Logger logger = LoggerFactory.getLogger(RegistroController.class);

    public RegistroController(ValidacionClient validacionClient) {
        this.validacionClient = validacionClient;
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest request) {
        try {
            ValidacionResponse dniResp = validacionClient.validarDni(request.getDni());
            if (dniResp == null || !dniResp.isValido()) {
                String msg = (dniResp != null && dniResp.getMensaje() != null) ? dniResp.getMensaje() : "El DNI no tiene un formato valido";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }

            ValidacionResponse correoResp = validacionClient.validarCorreo(request.getEmail());
            if (correoResp == null || !correoResp.isValido()) {
                String msg = (correoResp != null && correoResp.getMensaje() != null) ? correoResp.getMensaje() : "El correo no es válido o no pertenece a PUCP";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }

            // Si ambas validaciones son exitosas, solo respondemos OK.
            return ResponseEntity.ok().body("Registro validado");
        } catch (Exception ex) {
            logger.error("Error al comunicarse con validacion-service", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al validar datos");
        }
    }
}

