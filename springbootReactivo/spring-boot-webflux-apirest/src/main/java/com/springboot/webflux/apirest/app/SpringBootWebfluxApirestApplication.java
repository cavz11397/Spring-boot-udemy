package com.springboot.webflux.apirest.app;

import com.springboot.webflux.apirest.app.models.documents.Categoria;
import com.springboot.webflux.apirest.app.models.documents.Producto;
import com.springboot.webflux.apirest.app.services.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
public class SpringBootWebfluxApirestApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApirestApplication.class);

    @Autowired
    private ProductoService productoService;
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebfluxApirestApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        reactiveMongoTemplate.dropCollection("productos").subscribe();
        reactiveMongoTemplate.dropCollection("categorias").subscribe();

        Categoria electronico = new Categoria("Electronico");
        Categoria deporte = new Categoria("Deporte");
        Categoria computacion = new Categoria("Computacion");
        Categoria muebles = new Categoria("Muebles");

        //el thenMany es para incluir otro flujo una vez haya terminado el de categoria pero que este sea dele tipo Flux
        Flux.just(electronico, deporte, computacion, muebles)
                .flatMap(productoService::saveCategoria)
                .doOnNext(c -> log.info("Categoria creada: " + c.getNombre()))
                .thenMany(
                        Flux.just(
                                new Producto("SmartTv LG", 456.458, electronico),
                                new Producto("Camara Sony", 654.25, electronico),
                                new Producto("Silla pc", 458.45, muebles),
                                new Producto("Teclado", 200.45, computacion),
                                new Producto("CAMA", 456.26, muebles),
                                new Producto("Monitor", 789.5, computacion),
                                new Producto("PC GAMER", 985.6, computacion),
                                new Producto("Notebook", 312.0, computacion)
                        )
                                .flatMap(producto -> {
                                    producto.setCreateAt(new Date());
                                    return productoService.save(producto);
                                })
                )
                .subscribe(p -> log.info("insert: " + p.getId() + " " + p.getNombre()));
    }
}
