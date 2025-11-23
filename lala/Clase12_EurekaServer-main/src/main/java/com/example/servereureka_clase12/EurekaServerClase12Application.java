package com.example.servereureka_clase12;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerClase12Application {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerClase12Application.class, args);
    }

}
