package org.example.crudapi.controller;

import lombok.AllArgsConstructor;
import org.example.crudapi.dto.TokenResponse;
import org.example.crudapi.dto.UserCreateDto;
import org.example.crudapi.dto.UserDto;
import org.example.crudapi.dto.UserLoginDto;
import org.example.crudapi.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    UserDto register(@RequestBody UserCreateDto userCreateDto) {
        return authService.register(userCreateDto);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    TokenResponse login(@RequestBody UserLoginDto userLoginDto) {
        return authService.verify(userLoginDto);
    }
}
