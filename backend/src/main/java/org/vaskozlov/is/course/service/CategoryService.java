package org.vaskozlov.is.course.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.vaskozlov.is.course.bean.Category;
import org.vaskozlov.is.course.dto.CategoryDTO;
import org.vaskozlov.is.course.lib.Result;
import org.vaskozlov.is.course.repository.CategoryRepository;

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
