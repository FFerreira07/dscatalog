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

import com.ferreira.dscatalog.dto.CategoryDTO;
import com.ferreira.dscatalog.entities.Category;
import com.ferreira.dscatalog.factories.CategoryFactory;
import com.ferreira.dscatalog.repositories.CategoryRepository;
import com.ferreira.dscatalog.services.exceptions.DatabaseException;
import com.ferreira.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {

    @InjectMocks
    private CategoryService service;

    @Mock
    private CategoryRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;

    private Category category;
    private CategoryDTO categoryDTO;

    private PageImpl<Category> page;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;

        category = CategoryFactory.createCategory();
        categoryDTO = CategoryFactory.createCategoryDTO();

        page = new PageImpl<>(List.of(category));

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(category);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(repository.getReferenceById(existingId)).thenReturn(category);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);

        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
        Mockito.doNothing().when(repository).deleteById(existingId);
    }

     @Test
    public void insertShouldSaveAndReturnCategoryDTO() {
        categoryDTO.setId(null);
        CategoryDTO result = service.insert(categoryDTO);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());

        Mockito.verify(repository).save(ArgumentMatchers.any());
    }

    @Test
    public void findAllPagedShouldReturnCategoryDTOPage() {
        Pageable pageable = PageRequest.of(0, 1);

        Page<CategoryDTO> result = service.findAllPaged(pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(1, result.getSize());
        Mockito.verify(repository).findAll(pageable);
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

        Mockito.verify(repository).findById(nonExistingId);
    }

    @Test
    public void updateShouldUpdateAndReturnCategoryDTOWhenIdExists() {
        categoryDTO.setId(null);
        CategoryDTO result = service.update(existingId, categoryDTO);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());

        Mockito.verify(repository).save(category);
        Mockito.verify(repository).getReferenceById(existingId);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, categoryDTO);
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
