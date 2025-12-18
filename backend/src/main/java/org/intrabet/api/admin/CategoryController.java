package org.intrabet.api.admin;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.intrabet.bean.User;
import org.intrabet.dto.CategoryDTO;
import org.intrabet.service.CategoryService;

@RestController
@RequestMapping("/admin/category")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(
            @Valid @RequestBody CategoryDTO categoryDTO,
            @AuthenticationPrincipal User user
    ) {
        var creationResult = categoryService.create(categoryDTO, user.getId());

        if (creationResult.isError()) {
            return ResponseEntity
                    .badRequest()
                    .body(creationResult.getError());
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creationResult.getValue());
    }
}
