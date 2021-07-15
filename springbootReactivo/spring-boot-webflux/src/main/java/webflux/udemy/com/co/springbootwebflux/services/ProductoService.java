package webflux.udemy.com.co.springbootwebflux.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import webflux.udemy.com.co.springbootwebflux.models.documents.Categoria;
import webflux.udemy.com.co.springbootwebflux.models.documents.Producto;

public interface ProductoService {

    public Flux<Producto> findAll();

    public Flux<Producto> findAllConNombreUpperCase();

    public Flux<Producto> findAllConNombreUpperCaseConRepeat();

    public Mono<Producto> findById(String id);

    public Mono<Producto> save(Producto producto);

    public Mono<Void> delete(Producto producto);

    public Flux<Categoria> findAllCategoria();

    public Mono<Categoria> findCategoriaById(String id);

    public Mono<Categoria> saveCategoria(Categoria categoria);
}
