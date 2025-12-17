package org.intrabet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatedOutcomeDTO {
    @NotBlank(message = "Description of the outcome is required")
    private String description;
}
