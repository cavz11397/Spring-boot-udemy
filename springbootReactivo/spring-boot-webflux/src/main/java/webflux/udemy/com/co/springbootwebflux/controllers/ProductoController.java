package webflux.udemy.com.co.springbootwebflux.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import webflux.udemy.com.co.springbootwebflux.models.documents.Categoria;
import webflux.udemy.com.co.springbootwebflux.models.documents.Producto;
import webflux.udemy.com.co.springbootwebflux.services.ProductoService;

import javax.validation.Valid;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@SessionAttributes("producto")
@Controller
public class ProductoController {

    @Value("${config.uploads.path}")
    private String path;

    @Autowired
    private ProductoService productoService;

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    @ModelAttribute("categorias")
    public Flux<Categoria> categorias() {
        return productoService.findAllCategoria();
    }

    //CRUD

    //Aqui se utiliza una expresion regular en el path variable para poder añadir la extension
    @GetMapping("/uploads/img/{nombreFoto:.+}")
    public Mono<ResponseEntity<Resource>> verFoto(@PathVariable String nombreFoto) throws MalformedURLException {
        Path ruta = Paths.get(path)
                .resolve(nombreFoto)
                .toAbsolutePath();
        Resource imagen = new UrlResource(ruta.toUri());
        return Mono.just(
                ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""+imagen.getFilename()+"\"")
                .body(imagen)
        );
    }


    @GetMapping("/ver/{id}")
    public Mono<String> ver(@PathVariable("id") String id, Model model) {
        return productoService.findById(id)
                .doOnNext(p -> {
                    model.addAttribute("producto", p);
                    model.addAttribute("titulo", "Detalle del producto");
                })
                .switchIfEmpty(Mono.just(new Producto()))
                .flatMap(p -> {
                    if (p.getId() == null) {
                        return Mono.error(new InterruptedException("No existe el producto"));
                    }
                    return Mono.just(p);
                })
                .then(Mono.just("ver"))
                .onErrorResume(ex -> Mono.just("redirect:/listar?error=no+existe+el+producto+a+eliminar"));
    }

    //Las llaves dentro de los parametros del GetMapping son para decirle que responde a varias rutas
    @GetMapping({"/listar", "/"})
    public Mono<String> listar(Model model) {
        Flux<Producto> productoFlux = productoService.findAllConNombreUpperCase();
        productoFlux.subscribe(prod -> log.info(prod.getNombre()));
        model.addAttribute("productoFlux", productoFlux);
        model.addAttribute("titulo", "Listado de productos");
        return Mono.just("listar");
    }

    @GetMapping("/form")
    public Mono<String> crear(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("titulo", "formulario de producto");
        model.addAttribute("boton", "Crear");
        return Mono.just("form");
    }

    //    El SEssion es para poder editar en caso de que no querramos tener el hidden en la plantilla html
    //El valid esa para poder validar con la javax.validation
    //El binding result debe ir pegado al producto
    @PostMapping("/form")
    public Mono<String> guardar(@Valid @ModelAttribute("producto") Producto producto,
                                BindingResult result,
                                Model model,
                                @RequestPart FilePart file,
                                SessionStatus status) {
        System.out.println(result.hasErrors());
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Errores en formulario producto");
            model.addAttribute("boton", "Guardar");
            return Mono.just("form");
        } else {
            status.setComplete();
            Mono<Categoria> categoriaMono = productoService.findCategoriaById(producto.getCategoria().getId());
            return categoriaMono.flatMap(c -> {
                if (producto.getCreateAt() == null) {
                    producto.setCreateAt(new Date());
                }
                if (!file.filename().isEmpty()) {
                    producto.setFoto(UUID.randomUUID().toString() + "-" + file.filename()
                            .replace(" ", "")
                            .replace(":", "")
                            .replace("\\", "")
                    );
                }
                producto.setCategoria(c);
                return productoService.save(producto);
            })
                    .doOnNext(p -> {
                        log.info("Producto guardado: " + p.getNombre() + " ,id: " + p.getId());
                        log.info("Categoria asignada: " + p.getCategoria().getNombre() + " ,id: " + p.getCategoria().getId());
                    })
                    .flatMap(p -> {
                        if (!file.filename().isEmpty()) {
                            return file.transferTo(new File(path + p.getFoto()));
                        }
                        return Mono.empty();
                    })
                    .thenReturn("redirect:listar?success=producto+guardado+con+exito");
        }
    }

    @GetMapping("/eliminar/{id}")
    public Mono<String> eliminar(@PathVariable("id") String id) {
        return productoService.findById(id)
                .defaultIfEmpty(new Producto())
                .flatMap(p ->
                        (p.getId() == null)
                                ? Mono.error(new InterruptedException("No existe el producto a eliminar"))
                                : Mono.just(p)
                )
                .flatMap(p -> productoService.delete(p))
                .then(Mono.just("redirect:/listar?success=producto+eliminado+con+exito"))
                .onErrorResume(ex -> Mono.just("redirect:/listar?error=no+existe+el+producto+a+eliminar"));
    }

    @GetMapping("/form/{id}")
    public Mono<String> editar(@PathVariable("id") String id, Model model) {
        Mono<Producto> productoMono = productoService.findById(id)
                .doOnNext(p -> log.info("Producto: " + p.getNombre()))
                .defaultIfEmpty(new Producto());
        model.addAttribute("producto", productoMono);
        model.addAttribute("titulo", "editar producto");
        model.addAttribute("boton", "Guardar");
        return Mono.just("form");
    }

    //OTRA FORMA DE EDITAR
    @GetMapping("/form-v2/{id}")
    public Mono<String> editarV2(@PathVariable("id") String id, Model model) {
        return productoService.findById(id)
                .doOnNext(p -> {
                    log.info("Producto: " + p.getNombre());
                    model.addAttribute("producto", p);
                    model.addAttribute("titulo", "editar producto");
                    model.addAttribute("boton", "Editar");
                })
                .defaultIfEmpty(new Producto())
                .flatMap(p ->
                        (p.getId() == null)
                                ? Mono.error(new InterruptedException("No existe el producto"))
                                : Mono.just(p)
                )
                .thenReturn("form")
                .onErrorResume(ex -> Mono.just("redirect:/listar?error=no+existe+el+producto"));
    }

    //Data Driiver para el backPreassure
    @GetMapping("/listarDataDriver")
    public String listarDataDriver(Model model) {
        Flux<Producto> productoFlux = productoService
                .findAllConNombreUpperCase()
                .delayElements(Duration.ofSeconds(1));
        productoFlux.subscribe(prod -> log.info(prod.getNombre()));
        model.addAttribute("productoFlux", new ReactiveDataDriverContextVariable(productoFlux, 2));
        model.addAttribute("titulo", "Listado de productos");
        return "listar";
    }

    //Chunked para el backPreassure
    @GetMapping("/listar-chunked")
    public String listarChunked(Model model) {
        Flux<Producto> productoFlux = productoService.findAllConNombreUpperCaseConRepeat();
        model.addAttribute("productoFlux", new ReactiveDataDriverContextVariable(productoFlux, 2));
        model.addAttribute("titulo", "Listado de productos");
        return "listar-chunked";
    }

    @GetMapping("/listarFull")
    public String listarFull(Model model) {
        Flux<Producto> productoFlux = productoService.findAllConNombreUpperCaseConRepeat();
        model.addAttribute("productoFlux", new ReactiveDataDriverContextVariable(productoFlux, 2));
        model.addAttribute("titulo", "Listado de productos");
        return "listar";
    }
}
