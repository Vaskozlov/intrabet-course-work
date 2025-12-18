package org.intrabet.service;

import jakarta.transaction.Transactional;
import org.intrabet.bean.AdminLog;
import org.intrabet.bean.Category;
import org.intrabet.bean.User;
import org.intrabet.dto.CategoryDTO;
import org.intrabet.lib.Result;
import org.intrabet.repository.AdminLogRepository;
import org.intrabet.repository.CategoryRepository;
import org.intrabet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final AdminLogRepository adminLogRepository;
    private final UserRepository userRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, AdminLogRepository adminLogRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.adminLogRepository = adminLogRepository;
        this.userRepository = userRepository;
    }

    public boolean exists(@NonNull String name) {
        return categoryRepository.findByName(name.trim()).isPresent();
    }

    public Optional<Category> findByName(@NonNull String name) {
        return categoryRepository.findByName(name.trim());
    }

    @Transactional
    public Result<Category, String> create(@NonNull CategoryDTO categoryDTO, Long userId) {
        if (exists(categoryDTO.getName())) {
            return Result.error("Category already exists");
        }

        var user = userRepository.findById(userId).orElseThrow();

        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category = categoryRepository.save(category);

        adminLogRepository.save(
                AdminLog.builder()
                        .user(user)
                        .details(String.format("New category %s created by %s", category.getName(), user.getUsername()))
                        .build()
        );

        return Result.success(category);
    }
}
