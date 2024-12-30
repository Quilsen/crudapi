package org.example.crudapi.controller;

import org.example.crudapi.AbstractIntegrationTest;
import org.example.crudapi.dto.TokenResponse;
import org.example.crudapi.dto.UserCreateDto;
import org.example.crudapi.dto.UserDto;
import org.example.crudapi.dto.UserLoginDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/auth";
    }

    @Test
    @DisplayName("Should register a user and return UserDto")
    void shouldRegisterUserSuccessfully() {
        UserCreateDto userCreateDto = new UserCreateDto("authUser1", "password1");

        ResponseEntity<UserDto> response = restTemplate.postForEntity(
                baseUrl() + "/register",
                userCreateDto,
                UserDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isEqualTo("authUser1");
        assertThat(response.getBody().id()).isNotNull();
    }

    @Test
    @DisplayName("Should log in an existing user and return TokenResponse")
    void shouldLoginUserSuccessfully() {
        UserCreateDto userCreateDto = new UserCreateDto("authUser2", "password2");
        restTemplate.postForEntity(baseUrl() + "/register", userCreateDto, UserDto.class);

        UserLoginDto loginDto = new UserLoginDto("authUser2", "password2");

        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                baseUrl() + "/login",
                loginDto,
                TokenResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().access_token()).isNotBlank();
    }

    @Test
    @DisplayName("Should return 401 for invalid credentials")
    void shouldReturnUnauthorizedForInvalidCredentials() {
        UserLoginDto loginDto = new UserLoginDto("nonExistingUser", "wrongPassword");

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl() + "/login",
                loginDto,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).contains("Invalid username or password");
    }
}
