package com.springboot.webflux.apirest.app.models.dao;

import com.springboot.webflux.apirest.app.models.documents.Producto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductoDao extends ReactiveMongoRepository<Producto, String> {
}
