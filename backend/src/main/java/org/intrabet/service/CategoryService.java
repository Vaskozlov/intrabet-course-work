package org.intrabet.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.intrabet.bean.Category;
import org.intrabet.dto.CategoryDTO;
import org.intrabet.lib.Result;
import org.intrabet.repository.CategoryRepository;

import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public boolean exists(@NonNull String name) {
        return categoryRepository.findByName(name.trim()).isPresent();
    }

    public Optional<Category> findByName(@NonNull String name) {
        return categoryRepository.findByName(name.trim());
    }

    @Transactional
    public Result<Category, String> create(@NonNull CategoryDTO categoryDTO) {
        if (exists(categoryDTO.getName())) {
            return Result.error("Category already exists");
        }

        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        return Result.success(categoryRepository.save(category));
    }
}
