package org.intrabet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.intrabet.bean.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
