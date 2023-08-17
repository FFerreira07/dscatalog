package com.ferreira.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferreira.dscatalog.dto.CategoryDTO;
import com.ferreira.dscatalog.factories.CategoryFactory;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CategoryResourceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    public void insertShouldReturnStatusCreatedAndCategoryDTO() throws Exception {
        categoryDTO.setId(null);
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/categories")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @Test
    public void findAllPagedShouldReturnSortedPageWhenSortedPageableSortByNameExists() throws Exception {
        mockMvc.perform(get("/categories?page=0&size=3&sort=name")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content[0].name").value("Books"))
                .andExpect(jsonPath("$.content[1].name").value("Computers"))
                .andExpect(jsonPath("$.content[2].name").value("Electronics"));
    }

    @Test
    public void findByIdShouldReturnCategoryDTOWhendIdExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/categories/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @Test
    public void findByIdShouldReturnNoFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/categories/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void updateShouldReturnCategoryDTOWhenIdExists() throws Exception {

        CategoryDTO categoryDTO = CategoryFactory.createCategoryDTO();
        categoryDTO.setId(null);
        String expectedName = categoryDTO.getName();

        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        mockMvc.perform(put("/categories/{id}", existingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.name").value(expectedName));
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        CategoryDTO categoryDTO = CategoryFactory.createCategoryDTO();
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        mockMvc.perform(put("/categories/{id}", nonExistingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void deleteShouldReturnBadRequetWhenDependentId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/{id}", dependentId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
