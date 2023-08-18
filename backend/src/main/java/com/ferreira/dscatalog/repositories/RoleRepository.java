package com.ferreira.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ferreira.dscatalog.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
