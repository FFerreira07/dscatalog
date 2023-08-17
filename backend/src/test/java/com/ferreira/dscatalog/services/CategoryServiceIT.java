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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ferreira.dscatalog.dto.CategoryDTO;
import com.ferreira.dscatalog.entities.Category;
import com.ferreira.dscatalog.factories.CategoryFactory;
import com.ferreira.dscatalog.repositories.CategoryRepository;
import com.ferreira.dscatalog.services.exceptions.DatabaseException;
import com.ferreira.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class CategoryServiceIT {

    @Autowired
    private CategoryService service;

    @Autowired
    private CategoryRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;

    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 3L;

        categoryDTO = CategoryFactory.createCategoryDTO();
    }

    @Test
    public void insertShouldSaveAndReturnCategoryDTO() {
        categoryDTO.setId(null);
        CategoryDTO result = service.insert(categoryDTO);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
    }

    @Test
    public void findAllPagedShouldReturnPageWhenPageableExists() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<CategoryDTO> result = service.findAllPaged(pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(10, result.getSize());
    }

    @Test
    public void findAllPagedShouldReturnEmptyPageWhenPageableDoesNotExist() {

        Pageable pageable = PageRequest.of(999, 999);
        Page<CategoryDTO> result = service.findAllPaged(pageable);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void findAllPagedShouldReturnSortedPageWhenSortedPageableSortByNameExists() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("Name"));
        Page<CategoryDTO> result = service.findAllPaged(pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Books", result.getContent().get(0).getName());
        Assertions.assertEquals("Computers", result.getContent().get(1).getName());
        Assertions.assertEquals("Electronics", result.getContent().get(2).getName());
    }

    @Test
    public void findByIdShouldReturnCategoryDTOWhenIdExists() {
        CategoryDTO result = service.findById(existingId);

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
    public void updateShouldUpdateAndReturnCategoryDTOWhenIdExists() {
        categoryDTO.setId(null);
        CategoryDTO result = service.update(existingId, categoryDTO);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(existingId, result.getId());
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            categoryDTO.setId(null);
            service.update(nonExistingId, categoryDTO);
        });
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {
        service.delete(existingId);

        Optional<Category> result = repository.findById(existingId);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }

    @Test
    @Transactional(propagation = Propagation.NEVER) 
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });
    }
}
