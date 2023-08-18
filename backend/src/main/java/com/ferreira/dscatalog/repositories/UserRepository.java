package com.ferreira.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ferreira.dscatalog.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
