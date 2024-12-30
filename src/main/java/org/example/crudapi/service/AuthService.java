package org.example.crudapi.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.crudapi.dto.TokenResponse;
import org.example.crudapi.dto.UserCreateDto;
import org.example.crudapi.dto.UserDto;
import org.example.crudapi.dto.UserLoginDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {
    private UserService userService;
    private JwtUtil jwtUtil;
    AuthenticationManager authenticationManager;

    @Transactional
    public UserDto register(UserCreateDto userCreateDto) {
        return userService.registerUser(userCreateDto);
    }

    public TokenResponse verify(UserLoginDto userLoginDto) {
        try {
            Authentication authenticationRequest =
                    UsernamePasswordAuthenticationToken.unauthenticated(userLoginDto.username(), userLoginDto.password());
            Authentication authentication =
                    this.authenticationManager.authenticate(authenticationRequest);
            String token = "";
            if(authentication.isAuthenticated()) {
                token = jwtUtil.generateToken(userLoginDto.username());
            }
            return new TokenResponse(token);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password", e);
        }
    }
}
