package org.intrabet.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.intrabet.dto.UserRegistrationDTO;
import org.intrabet.lib.Result;
import org.intrabet.repository.UserRepository;

@Service
public class NewUserValidator {
    private final UserRepository userRepository;

    @Autowired
    public NewUserValidator(UserRepository userRepository) {
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
