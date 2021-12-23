package com.example.demospringbootall.repositories;

import com.example.demospringbootall.common.ERole;
import com.example.demospringbootall.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
