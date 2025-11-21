package org.vaskozlov.is.course.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vaskozlov.is.course.dto.UserRegistrationDTO;
import org.vaskozlov.is.course.repository.UserRepository;
import org.vaskozlov.is.course.service.RegistrationValidator;

@RestController
@RequestMapping("/auth")
public class UserCreation {
    private final RegistrationValidator registrationValidator;

    private final UserRepository userRepository;

    @Autowired
    public UserCreation(RegistrationValidator registrationValidator, UserRepository userRepository) {
        this.registrationValidator = registrationValidator;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        var validationResult = registrationValidator.validate(registrationDTO);

        if (validationResult.isError()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(validationResult.getError());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
