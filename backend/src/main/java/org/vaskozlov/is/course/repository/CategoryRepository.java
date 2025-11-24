package org.vaskozlov.is.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaskozlov.is.course.bean.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
