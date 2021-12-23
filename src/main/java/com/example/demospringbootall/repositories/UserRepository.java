package com.example.demospringbootall.repositories;

import com.example.demospringbootall.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findAllByEmail(String email);
    Boolean existsByUsername(String user_name);
    Boolean existsByEmail(String email);
}
