package org.intrabet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.intrabet.bean.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByPasswordHash(String passwordHash);
}
