package com.ferreira.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ferreira.dscatalog.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
