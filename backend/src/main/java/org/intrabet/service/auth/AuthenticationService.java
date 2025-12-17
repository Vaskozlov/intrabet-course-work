package org.intrabet.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.intrabet.bean.User;
import org.intrabet.dto.UserLoginDTO;
import org.intrabet.dto.UserRegistrationDTO;
import org.intrabet.lib.Result;
import org.intrabet.repository.UserRepository;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final NewUserValidator newUserValidator;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(
            UserRepository userRepository,
            NewUserValidator newUserValidator,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.newUserValidator = newUserValidator;
        this.passwordEncoder = passwordEncoder;
    }

    public Result<User, String> register(UserRegistrationDTO registrationDTO) {
        var validationResult = newUserValidator.validate(registrationDTO);

        if (validationResult.isError()) {
            return Result.error(validationResult.getError());
        }

        User user = User.builder()
                .username(registrationDTO.getUsername())
                .email(registrationDTO.getEmail())
                .passwordHash(passwordEncoder.encode(registrationDTO.getPassword()))
                .build();

        userRepository.save(user);

        return Result.success(userRepository.save(user));
    }

    public Result<User, String> login(UserLoginDTO loginDTO) {
        var userOptional = userRepository
                .findByEmail(loginDTO.getLoginOrEmail())
                .or(() -> userRepository.findByUsername(loginDTO.getLoginOrEmail()));

        if (userOptional.isEmpty()) {
            return Result.error("Invalid login or password");
        }

        var user = userOptional.get();

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return Result.error("Invalid login or password");
        }

        return Result.success(user);
    }
}
