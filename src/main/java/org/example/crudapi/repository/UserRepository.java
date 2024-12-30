package org.example.crudapi.repository;

import org.example.crudapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    @Override
    boolean existsById(Long Id);

    @Override
    void deleteById(Long Id);
}
