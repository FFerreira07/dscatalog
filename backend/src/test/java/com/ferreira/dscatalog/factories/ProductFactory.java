package com.ferreira.dscatalog.factories;

import java.time.Instant;

import com.ferreira.dscatalog.dto.ProductDTO;
import com.ferreira.dscatalog.entities.Category;
import com.ferreira.dscatalog.entities.Product;

public class ProductFactory {
    
    public static Product createProduct(){
        Product product = new Product(1L, "null", "null", 0.0, "null", Instant.parse("2020-07-13T20:50:07.12345Z"));
        product.getCategories().add(new Category(1L, "null"));

        return product;
    }

    public static ProductDTO createProductDTO(){
        Product product = createProduct();
        return new ProductDTO(product, product.getCategories());
    }
}
