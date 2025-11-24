package org.vaskozlov.is.course.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDTO {
    @NotBlank(message = "Login or email is required")
    private String loginOrEmail;

    @NotBlank(message = "Password is required")
    private String password;
}
