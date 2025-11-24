package org.vaskozlov.is.course.api.auth;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vaskozlov.is.course.bean.User;
import org.vaskozlov.is.course.dto.AuthenticationResponseDTO;
import org.vaskozlov.is.course.dto.UserLoginDTO;
import org.vaskozlov.is.course.dto.UserRegistrationDTO;
import org.vaskozlov.is.course.service.auth.AuthenticationService;
import org.vaskozlov.is.course.service.auth.JwtTokenService;
import org.vaskozlov.is.course.service.auth.TokenService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final TokenService tokenService;
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(JwtTokenService jwtTokenService, AuthenticationService authenticationService) {
        this.tokenService = jwtTokenService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            var registrationResult = authenticationService.register(registrationDTO);

            if (registrationResult.isError()) {
                return ResponseEntity
                        .badRequest()
                        .body(registrationResult.getError());
            }

            var user = registrationResult.getValue();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthenticationResponseDTO(
                            tokenService.getTokenType(),
                            tokenService.createToken(user)
                    ));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginDTO loginDTO) {
        try {
            var loginResult = authenticationService.login(loginDTO);

            if (loginResult.isError()) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(loginResult.getError());
            }

            User user = loginResult.getValue();

            return ResponseEntity.ok(
                    new AuthenticationResponseDTO(
                            tokenService.getTokenType(),
                            tokenService.createToken(user)
                    ));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }


}
