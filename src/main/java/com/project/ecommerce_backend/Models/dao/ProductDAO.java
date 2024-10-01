package com.project.ecommerce_backend.Models.dao;

import com.project.ecommerce_backend.Models.Product;
import org.springframework.data.repository.ListCrudRepository;

public interface ProductDAO extends ListCrudRepository<Product, Long> {
}
