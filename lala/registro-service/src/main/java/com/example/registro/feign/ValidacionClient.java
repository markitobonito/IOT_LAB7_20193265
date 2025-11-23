package com.example.registro.feign;

import com.example.registro.dto.ValidacionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "validacion-service")
public interface ValidacionClient {

    @GetMapping("/validar/dni/{dni}")
    ValidacionResponse validarDni(@PathVariable("dni") String dni);

    @GetMapping("/validar/correo/{correo}")
    ValidacionResponse validarCorreo(@PathVariable("correo") String correo);
}

