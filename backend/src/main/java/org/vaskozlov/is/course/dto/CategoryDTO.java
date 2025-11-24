package org.vaskozlov.is.course.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDTO {
    @NotBlank(message = "Category name is required")
    String name;

    String description;
}
