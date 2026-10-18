package com.grupoj.Razonando_Back.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.grupoj.Razonando_Back.dto.LoginRequest;
import com.grupoj.Razonando_Back.dto.LoginResponse;
import com.grupoj.Razonando_Back.dto.StudentRegistrationRequest;
import com.grupoj.Razonando_Back.dto.StudentRegistrationResponse;
import com.grupoj.Razonando_Back.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<StudentRegistrationResponse> register(
            @Valid @RequestBody StudentRegistrationRequest registrationRequest) {
        return authService.registerStudent(registrationRequest);
    }

}