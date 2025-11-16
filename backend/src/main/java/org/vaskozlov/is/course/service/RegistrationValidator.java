package org.vaskozlov.is.course.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.vaskozlov.is.course.lib.Result;
import org.vaskozlov.is.course.repository.UserRepository;

@ApplicationScoped
public class RegistrationValidator {
    private static final JsonbConfig JSONB_CONFIG = new JsonbConfig();
    private static final Jsonb JSONB = JsonbBuilder.create(JSONB_CONFIG);

    @Inject
    private UserRepository userRepository;

    public Result<Void, String> validate(JsonObject data) {
        if (data == null) {
            return Result.error("Failed to parse json");
        }

        String username = data.getString("username", null);

        if (username == null) {
            return Result.error("Username is missed or invalid");
        }

        String password = data.getString("password", null);
        if (password == null) {
            return Result.error("Password is missed or invalid");
        }

        String email = data.getString("email", null);
        if (email == null) {
            return Result.error("Email is missed or invalid");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            return Result.error("Username already exists");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return Result.error("Email already exists");
        }

        return Result.success(null);
    }
}
