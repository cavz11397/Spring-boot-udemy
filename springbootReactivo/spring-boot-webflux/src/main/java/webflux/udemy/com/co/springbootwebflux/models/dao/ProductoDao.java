package webflux.udemy.com.co.springbootwebflux.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import webflux.udemy.com.co.springbootwebflux.models.documents.Producto;

public interface ProductoDao extends ReactiveMongoRepository<Producto, String> {
}
