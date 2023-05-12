package com.ferreira.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ferreira.dscatalog.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
