package com.example.validacionservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ValidacionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ValidacionServiceApplication.class, args);
    }
}

