package com.ferreira.dscatalog.services;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.ferreira.dscatalog.dto.ProductDTO;
import com.ferreira.dscatalog.entities.Product;
import com.ferreira.dscatalog.factories.ProductFactory;
import com.ferreira.dscatalog.repositories.ProductRepository;
import com.ferreira.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ProductServiceIT {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;

    private ProductDTO productDTO;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;

        productDTO = ProductFactory.createProductDTO();
    }

    @Test
    public void insertShouldSaveAndReturnProductDTO() {
        productDTO.setId(null);
        ProductDTO result = service.insert(productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
    }

    @Test
    public void findAllPagedShouldReturnPageWhenPageableExists() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(10, result.getSize());
    }

    @Test
    public void findAllPagedShouldReturnEmptyPageWhenPageableDoesNotExist() {

        Pageable pageable = PageRequest.of(999, 999);
        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void findAllPagedShouldReturnSortedPageWhenSortedPageableSortByNameExists() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("Name"));
        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {
        ProductDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
    }

    @Test
    public void updateShouldUpdateAndReturnProdutDTOWhenIdExists() {
        productDTO.setId(null);
        ProductDTO result = service.update(existingId, productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(existingId, result.getId());
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productDTO.setId(null);
            service.update(nonExistingId, productDTO);
        });
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {
        service.delete(existingId);

        Optional<Product> result = repository.findById(existingId);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }

}
