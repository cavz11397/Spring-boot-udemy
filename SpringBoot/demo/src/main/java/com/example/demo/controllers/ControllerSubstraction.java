package com.example.demo.controllers;

import com.example.demo.services.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

public class ControllerSubstraction {

    @Autowired
    private CalculatorService calculatorService;

    public ControllerSubstraction(){
        System.out.println("creando ControllerSubstraction");
    }

    @GetMapping("/restar")
    public String restar(){
        int numA = 5;
        int numB = 6;
        return ("la resta es: "+calculatorService.resta(numA,numB));
    }
}
