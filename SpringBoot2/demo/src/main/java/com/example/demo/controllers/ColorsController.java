package com.example.demo.controllers;

import com.example.demo.model.Color;
import com.example.demo.model.Response;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class ColorsController {

    /*
     * PARAMETROS POR URL
     * PathVariable
     * podemos tener varios parametros en una
     * @GetMapping("/colors/{id}/{name}") En este caso id y name son variables y se llaman asi
     * public Color getColorById(@PathVariable("id") Integer id, @PathVariable("name") String name)
     * */

    /*
     * PARAMETROS POR URL QUERY
     * Tenemos la URL normal pero pasamos los parametros con un signo de interrogacion de cierre mas el nombre mas el valor
     * si queremos colocar mas se pone el ampersand
     * localhost:8080/colors?name=negro
     * @GetMapping("/colorsQuery")
     * public Color getColorByNameQuery(@RequestParam("name") String name)
     * */

    private static List<Color> colores = new ArrayList<>();

    public ColorsController() {
        colores.add(new Color(1, "negro"));
        colores.add(new Color(2, "blanco"));
    }

    @GetMapping("/colors")
    public List<Color> getAllColors() {
        return colores;
    }

    @GetMapping("/colors/{id}")
    public Color getColorById(@PathVariable("id") Integer id) {
        return colores.stream()
                .filter(c -> c.getIdColor().equals(id))
                .findFirst()
                .get();
    }

    @GetMapping("/colors/{id}/{name}")
    public Color getColorByIdAndName(@PathVariable("id") Integer id, @PathVariable("name") String name) {
        return colores.stream()
                .filter(c -> c.getIdColor().equals(id))
                .filter(c -> c.getColor().equals(name))
                .findFirst()
                .get();
    }

    @GetMapping("/colorsQuery")
    public Color getColorByNameQuery(@RequestParam("name") String name) {
        return colores.stream()
                .filter(c -> c.getColor().equals(name))
                .findFirst()
                .get();
    }

    //Con @RequestBody la peticion esperara un objeto del tipo Color
    @PostMapping("/colors")
    public Response saveColor(@RequestBody Color color) {
        Response response = new Response();
        colores.add(color);
        response.setCode(1L);
        response.setMessage("se guardo el color " + color.getColor());
        response.setTimestamp(new Date().getTime());
        return response;
    }

    @PutMapping("/colors/{id}")
    public String updateColor(@PathVariable("id") Integer id, @RequestBody Color color) {
        colores.stream()
                .filter(c -> c.getIdColor().equals(id))
                .findFirst()
                .get()
                .setColor(color.getColor());
        return ("se actualizo el color " + color.getColor());
    }

    @DeleteMapping("/colors/{id}")
    public String deleteColor(@PathVariable("id") Integer id) {
        Color colorToDelete = colores.stream()
                .filter(c -> c.getIdColor().equals(id))
                .findFirst()
                .get();
        colores.remove(colorToDelete);
        return ("Se elmino el color " + colorToDelete.getColor());
    }

}
