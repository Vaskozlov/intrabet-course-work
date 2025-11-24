package org.vaskozlov.is.course.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaskozlov.is.course.service.CategoryService;

@Component
public class CategoryValidator implements ConstraintValidator<ValidCategory, String> {
    private final CategoryService categoryService;

    @Autowired
    public CategoryValidator(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        return categoryService.exists(value);
    }
}
