package com.ferreira.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ferreira.dscatalog.entities.Product;
import com.ferreira.dscatalog.factories.ProductFactory;

@DataJpaTest
public class ProductRepostioryTests {

    @Autowired
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Product product;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;

        product = ProductFactory.createProduct();
    }

    @Test
    public void saveShouldPersistAndReturnProduct() {
        product.setId(null);

        product = repository.save(product);

        Assertions.assertNotNull(product.getId());

    }

    @Test
    public void findAllShouldReturnProductPageWhenPageableExists() {
        Pageable pageable = PageRequest.of(0, 1);

        Page<Product> result = repository.findAll(pageable);

        Assertions.assertNotNull(result);
    }

    @Test
    public void findByIdShouldReturnNotEmptyProductOptionalWhenIdExists() {

        Optional<Product> result = repository.findById(existingId);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertNotNull(result.get().getId());
    }

    @Test
    public void findByIdShouldReturnEmptyProductOptionalWhenIdDoesNotExist(){

        Optional<Product> result = repository.findById(nonExistingId);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void deleteByIdShouldDeleteObjectWhenIdExists() {

        repository.deleteById(existingId);

        Optional<Product> result = repository.findById(existingId);
        Assertions.assertTrue(result.isEmpty());
    }
}
