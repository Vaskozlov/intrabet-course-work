package org.vaskozlov.is.course.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaskozlov.is.course.dto.UserRegistrationDTO;
import org.vaskozlov.is.course.lib.Result;
import org.vaskozlov.is.course.repository.UserRepository;

@Service
public class RegistrationValidator {
    private final UserRepository userRepository;

    @Autowired
    public RegistrationValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Result<Void, String> validate(UserRegistrationDTO registrationDTO) {
        if (userRepository.findByUsername(registrationDTO.getUsername()).isPresent()) {
            return Result.error("Username already exists");
        }

        if (userRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            return Result.error("Email already exists");
        }

        // TODO: validate password

        return Result.success(null);
    }
}
