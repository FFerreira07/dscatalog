package com.ferreira.dscatalog.factories;

import com.ferreira.dscatalog.dto.CategoryDTO;
import com.ferreira.dscatalog.entities.Category;

public class CategoryFactory {
    
    public static Category createCategory(){
        return new Category(1L, "null");
    }

    public static CategoryDTO createCategoryDTO(){
        return new CategoryDTO(createCategory());
    }
}
