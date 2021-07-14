package webflux.udemy.com.co.springbootwebflux.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import webflux.udemy.com.co.springbootwebflux.SpringBootWebfluxApplication;
import webflux.udemy.com.co.springbootwebflux.models.dao.ProductoDao;
import webflux.udemy.com.co.springbootwebflux.models.documents.Producto;

import java.time.Duration;

@Controller
public class ProductoController {

    @Autowired
    private ProductoDao productoDao;

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);


    //Las llaves dentro de los parametros del GetMapping son para decirle que responde a varias rutas
    @GetMapping({"/listar", "/"})
    public String listar(Model model) {
        Flux<Producto> productoFlux = productoDao.findAll()
                .map(producto ->{
                    producto.setNombre(producto.getNombre().toUpperCase());
                    return producto;
                });
        productoFlux.subscribe(prod -> log.info(prod.getNombre()));
        model.addAttribute("productoFlux", productoFlux);
        model.addAttribute("titulo", "Listado de productos");
        return "listar";
    }

    //Data Driiver para el backPreassure
    @GetMapping("/listarDataDriver")
    public String listarDataDriver(Model model) {
        Flux<Producto> productoFlux = productoDao.findAll()
                .map(producto ->{
                    producto.setNombre(producto.getNombre().toUpperCase());
                    return producto;
                }).delayElements(Duration.ofSeconds(1));
        productoFlux.subscribe(prod -> log.info(prod.getNombre()));
        model.addAttribute("productoFlux", new ReactiveDataDriverContextVariable(productoFlux, 2 ));
        model.addAttribute("titulo", "Listado de productos");
        return "listar";
    }

    //Chunked para el backPreassure
    @GetMapping("/listar-chunked")
    public String listarChunked(Model model) {
        Flux<Producto> productoFlux = productoDao.findAll()
                .map(producto ->{
                    producto.setNombre(producto.getNombre().toUpperCase());
                    return producto;
                }).repeat(5000);
        model.addAttribute("productoFlux", new ReactiveDataDriverContextVariable(productoFlux, 2 ));
        model.addAttribute("titulo", "Listado de productos");
        return "listar-chunked";
    }

    @GetMapping("/listarFull")
    public String listarFull(Model model) {
        Flux<Producto> productoFlux = productoDao.findAll()
                .map(producto ->{
                    producto.setNombre(producto.getNombre().toUpperCase());
                    return producto;
                }).repeat(5000);
        model.addAttribute("productoFlux", new ReactiveDataDriverContextVariable(productoFlux, 2 ));
        model.addAttribute("titulo", "Listado de productos");
        return "listar";
    }
}
