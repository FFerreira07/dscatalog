package com.ferreira.dscatalog.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.ferreira.dscatalog.dto.ProductDTO;
import com.ferreira.dscatalog.entities.Category;
import com.ferreira.dscatalog.entities.Product;
import com.ferreira.dscatalog.factories.ProductFactory;
import com.ferreira.dscatalog.repositories.CategoryRepository;
import com.ferreira.dscatalog.repositories.ProductRepository;
import com.ferreira.dscatalog.services.exceptions.DatabaseException;
import com.ferreira.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;

    private Product product;
    private ProductDTO productDTO;

    private PageImpl<Product> page;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;

        product = ProductFactory.createProduct();
        productDTO = ProductFactory.createProductDTO();

        page = new PageImpl<>(List.of(product));

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);

        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(new Category(1L, "null"));

        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
        Mockito.doNothing().when(repository).deleteById(existingId);
    }

     @Test
    public void insertShouldSaveAndReturnProductDTO() {
        productDTO.setId(null);
        ProductDTO result = service.insert(productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());

        Mockito.verify(repository).save(ArgumentMatchers.any());
        Mockito.verify(categoryRepository).getReferenceById(existingId);
    }

    @Test
    public void findAllPagedShouldReturnProductDTOPage() {
        Pageable pageable = PageRequest.of(0, 1);

        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(1, result.getSize());
        Mockito.verify(repository).findAll(pageable);
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

        Mockito.verify(repository).findById(nonExistingId);
    }

    @Test
    public void updateShouldUpdateAndReturnProdutDTOWhenIdExists() {
        productDTO.setId(null);
        ProductDTO result = service.update(existingId, productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());

        Mockito.verify(repository).save(product);
        Mockito.verify(repository).getReferenceById(existingId);
        Mockito.verify(categoryRepository).getReferenceById(existingId);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, productDTO);
        });

        Mockito.verify(repository).getReferenceById(nonExistingId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {

        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        Mockito.verify(repository).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });

        Mockito.verify(repository).deleteById(dependentId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });

    }

}
