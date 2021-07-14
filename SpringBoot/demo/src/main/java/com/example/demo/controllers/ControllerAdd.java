package com.example.demo.controllers;

import com.example.demo.services.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerAdd {

    @Autowired
    private CalculatorService calculatorService;

    public ControllerAdd(){
        System.out.println("creando controllerAdd");
    }

    @GetMapping("/sumar")
    public String sumar(){
        int numA = 5;
        int numB = 6;
        return ("la suma es: "+calculatorService.sum(numA,numB));
    }

}
