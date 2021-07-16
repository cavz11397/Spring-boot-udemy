package com.springboot.webflux.apirest.app.controllers;

import com.springboot.webflux.apirest.app.models.documents.Producto;
import com.springboot.webflux.apirest.app.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Value("${config.uploads.path}")
    private String path;

    @Autowired
    private ProductoService productoService;

    @PostMapping("/upload/{id}")
    public Mono<ResponseEntity<Producto>> upload(@PathVariable("id") String id, @RequestPart FilePart filePart) {
        return productoService.findById(id)
                .flatMap(p -> {
                    p.setFoto(UUID.randomUUID().toString() + "-" + filePart.filename()
                            .replace(" ", "")
                            .replace("-", "")
                            .replace("\\", "")
                    );
                    return filePart.transferTo(new File(path + p.getFoto()))
                            .then(productoService.save(p));
                })
                .map(p -> ResponseEntity.ok(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("v2")
    public Mono<ResponseEntity<Producto>> crearConFoto(Producto producto, @RequestPart FilePart filePart) {
        if (producto.getCreateAt() == null) {
            producto.setCreateAt(new Date());
        }
        producto.setFoto(UUID.randomUUID().toString() + "-" + filePart.filename()
                .replace(" ", "")
                .replace("-", "")
                .replace("\\", "")
        );
        return filePart.transferTo(new File(path + producto.getFoto()))
                .then(productoService.save(producto))
                .map(p -> ResponseEntity.created(URI.create("/api/productos/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                );
    }

/*
    @GetMapping
    public Flux<Producto> lista() {
        return productoService.findAll();
    }
*/

    @GetMapping
    public Mono<ResponseEntity<Flux<Producto>>> listaConResponseEntity() {
        //Lo que hace el ok es devolver el status a  200 ok
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(productoService.findAll())
        );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Producto>> ver(@PathVariable("id") String id) {
        return productoService.findById(id)
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> crear(@Valid @RequestBody Mono<Producto> monoProducto) {

        Map<String, Object> respuesta = new HashMap<>();

        return monoProducto.flatMap(producto -> {
            if (producto.getCreateAt() == null) {
                producto.setCreateAt(new Date());
            }
            return productoService.save(producto)
                    .map(p -> {
                        respuesta.put("producto", p);
                        respuesta.put("mensaje", "Producto creado con exito");
                        respuesta.put("timestamp", new Date());
                        return ResponseEntity.created(URI.create("/api/productos/".concat(p.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(respuesta);
                    });
        })
                .onErrorResume(t -> Mono.just(t).cast(WebExchangeBindException.class)
                        .flatMap(e -> Mono.just(e.getFieldErrors()))
                        .flatMapMany(Flux::fromIterable)
                        .map(fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                        .collectList()
                        .flatMap(list -> {
                            respuesta.put("errors", list);
                            respuesta.put("timestamp", new Date());
                            respuesta.put("status",HttpStatus.BAD_REQUEST.value());
                            return Mono.just(ResponseEntity.badRequest()
                                    .body(respuesta));
                        })
                );
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Producto>> editar(@RequestBody Producto producto, @PathVariable("id") String id) {
        return productoService.findById(id)
                .flatMap(p -> {
                    p.setNombre(producto.getNombre());
                    p.setPrecio(producto.getPrecio());
                    p.setCategoria(producto.getCategoria());
                    return productoService.save(p);
                })
                .map(p -> ResponseEntity.created(URI.create("/api/productos/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> eliminar(@PathVariable("id") String id) {
        return productoService.findById(id)
                .flatMap(p -> productoService.delete(p)
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }
}
