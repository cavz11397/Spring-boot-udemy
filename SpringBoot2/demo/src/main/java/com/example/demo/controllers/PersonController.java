package com.example.demo.controllers;

import com.example.demo.model.Person;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class PersonController {

    private static List<Person> persona = new ArrayList<>();

    public PersonController() {
        persona.add(new Person("1234", "CC", "Juan", "Diaz", "123456789"));
        persona.add(new Person("1235", "CC", "Jacobo", "Martinez", "123456789"));
        persona.add(new Person("1236", "CE", "JHon", "Guerrero", "123456789"));
        persona.add(new Person("1237", "CC", "Angelica", "Orozco", "123456789"));
    }

    /**
     * Tener tantos requestParams no es buenop, spring nos deja utilizar esto como un map
     * como se hara en searchPerson2
     */

    @GetMapping("/persons")
    public List<Person> searchPerson(@RequestParam("identification") String identification,
                                     @RequestParam(value = "identificationType", required = false) String identificationType,
                                     @RequestParam(value = "name", required = false) String name,
                                     @RequestParam(value = "lastName", required = false) String lastName,
                                     @RequestParam(value = "phone", required = false) String phone
    ) {
        return persona.stream()
                .filter(p -> identification == null || identification.equals(p.getIdentification()))
                .filter(p -> identificationType == null || identificationType.equals(p.getIdentificationType()))
                .filter(p -> name == null || name.equals(p.getName()))
                .filter(p -> lastName == null || lastName.equals(p.getLastName()))
                .filter(p -> phone == null || phone.equals(p.getPhone()))
                .collect(Collectors.toList());
    }

    @GetMapping("/persons2")
    public List<Person> searchPerson2(@RequestParam Map<String, String> parameters) {
        return persona.stream()
                .filter(p -> parameters.get("identification") == null || parameters.get("identification").equals(p.getIdentification()))
                .filter(p -> parameters.get("identificationType") == null || parameters.get("identificationType").equals(p.getIdentificationType()))
                .filter(p -> parameters.get("name") == null || parameters.get("name").equals(p.getName()))
                .filter(p -> parameters.get("lastName") == null || parameters.get("lastName").equals(p.getLastName()))
                .filter(p -> parameters.get("phone") == null || parameters.get("phone").equals(p.getPhone()))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/persons3", produces = MediaType.TEXT_PLAIN_VALUE)
    public byte[] searchPerson3(@RequestParam Map<String, String> parameters) {
        return persona.stream()
                .filter(p -> parameters.get("identification") == null || parameters.get("identification").equals(p.getIdentification()))
                .filter(p -> parameters.get("identificationType") == null || parameters.get("identificationType").equals(p.getIdentificationType()))
                .filter(p -> parameters.get("name") == null || parameters.get("name").equals(p.getName()))
                .filter(p -> parameters.get("lastName") == null || parameters.get("lastName").equals(p.getLastName()))
                .filter(p -> parameters.get("phone") == null || parameters.get("phone").equals(p.getPhone()))
                .collect(Collectors.toList())
                .toString()
                .getBytes();
    }

}
