package com.example.demo.controllers;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
public class FileController {

    @PostMapping("/uploadFile")
    public String upload(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path targetLocation = Paths.get("C:\\Users\\cristian.vasquez\\Documents\\cursos de spring\\SpringBoot2\\demo" + fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        return ("Se subio en archivo " + fileName);
    }

    @GetMapping(value = "download", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody
    byte[] downloadImage() throws IOException {

        File file = new File("C:\\Users\\cristian.vasquez\\Downloads\\ingles.PNG");
        byte[] bytes = new byte[(int) file.length()];
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
        dataInputStream.readFully(bytes);
        return bytes;
    }

}
