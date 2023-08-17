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

import com.ferreira.dscatalog.entities.Category;
import com.ferreira.dscatalog.factories.CategoryFactory;

@DataJpaTest
public class CategoryRepostioryTests {

    @Autowired
    private CategoryRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Category category;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;

        category = CategoryFactory.createCategory();
    }

    @Test
    public void saveShouldPersistAndReturnCategory() {
        category.setId(null);

        category = repository.save(category);

        Assertions.assertNotNull(category.getId());

    }

    @Test
    public void findAllShouldReturnCategoryPageWhenPageableExists() {
        Pageable pageable = PageRequest.of(0, 1);

        Page<Category> result = repository.findAll(pageable);

        Assertions.assertNotNull(result);
    }

    @Test
    public void findByIdShouldReturnNotEmptyCategoryOptionalWhenIdExists() {

        Optional<Category> result = repository.findById(existingId);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertNotNull(result.get().getId());
    }

    @Test
    public void findByIdShouldReturnEmptyCategoryOptionalWhenIdDoesNotExist(){

        Optional<Category> result = repository.findById(nonExistingId);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void deleteByIdShouldDeleteObjectWhenIdExists() {

        repository.deleteById(existingId);

        Optional<Category> result = repository.findById(existingId);
        Assertions.assertTrue(result.isEmpty());
    }
}
