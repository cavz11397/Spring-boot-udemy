package webflux.udemy.com.co.springbootwebflux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import webflux.udemy.com.co.springbootwebflux.models.dao.ProductoDao;
import webflux.udemy.com.co.springbootwebflux.models.documents.Producto;

import java.util.Date;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);

    @Autowired
    private ProductoDao productoDao;
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebfluxApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        reactiveMongoTemplate.dropCollection("productos").subscribe();

        Flux.just(
                new Producto("SmartTv LG", 456.458),
                new Producto("Camara Sony", 654.25),
                new Producto("Silla pc", 458.45),
                new Producto("Teclado", 200.45),
                new Producto("CAMA", 456.26),
                new Producto("Monitor", 789.5),
                new Producto("PC GAMER", 985.6),
                new Producto("Notebook", 312.0)
        )
                .flatMap(producto -> {
                    producto.setCreateAt(new Date());
                    return productoDao.save(producto);
                })
                .subscribe(p -> log.info("insert: " + p.getId()+" "+p.getNombre()));
    }
}
