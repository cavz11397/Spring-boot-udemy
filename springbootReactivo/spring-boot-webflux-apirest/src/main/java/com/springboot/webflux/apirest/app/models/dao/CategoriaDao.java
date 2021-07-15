package com.springboot.webflux.apirest.app.models.dao;

import com.springboot.webflux.apirest.app.models.documents.Categoria;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoriaDao extends ReactiveMongoRepository<Categoria, String> {
}
