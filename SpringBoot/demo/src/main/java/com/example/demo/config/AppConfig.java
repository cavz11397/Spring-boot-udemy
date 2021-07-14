package com.example.demo.config;

import com.example.demo.services.CalculatorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public CalculatorService configCalculator(){
        System.out.println("Creando bean CalculatorService");
        return new CalculatorService();
    }
}
