package org.example.crudapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.crudapi.AbstractIntegrationTest;
import org.example.crudapi.dto.ApiResponse;
import org.example.crudapi.dto.TokenResponse;
import org.example.crudapi.dto.UserCreateDto;
import org.example.crudapi.dto.UserDto;
import org.example.crudapi.dto.UserLoginDto;
import org.example.crudapi.dto.UserUpdateDto;
import org.example.crudapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    private String authUrl() {
        return "http://localhost:" + port + "/auth";
    }

    private String usersUrl() {
        return "http://localhost:" + port + "/users";
    }

    private UserDto registerUser(String username, String password) {
        UserCreateDto userCreateDto = new UserCreateDto(username, password);
        ResponseEntity<UserDto> response = restTemplate.postForEntity(
                authUrl() + "/register",
                userCreateDto,
                UserDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody();
    }

    private String loginAndGetToken(String username, String password) {
        UserLoginDto loginDto = new UserLoginDto(username, password);
        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                authUrl() + "/login",
                loginDto,
                TokenResponse.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody().access_token();
    }

    @Test
    @DisplayName("GET /users - should return an empty list if the database is empty")
    void shouldReturnEmptyListIfDatabaseIsEmpty() {
        registerUser("adminUser", "adminPass");
        String token = loginAndGetToken("adminUser", "adminPass");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<UserDto[]> response = restTemplate.exchange(
                usersUrl(),
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                UserDto[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].username()).isEqualTo("adminUser");
    }

    @Test
    @DisplayName("Should register user1 and retrieve it in GET /users")
    void shouldCreateUserAndRetrieveIt() {
        registerUser("operator", "opPass");
        String token = loginAndGetToken("operator", "opPass");

        registerUser("user1", "password1");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<UserDto[]> response = restTemplate.exchange(
                usersUrl(),
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                UserDto[].class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDto[] body = response.getBody();
        assertThat(body).hasSize(2);
        assertThat(body)
                .extracting(UserDto::username)
                .containsExactlyInAnyOrder("operator", "user1");
    }

    @Test
    @DisplayName("Should update user's account (PUT /users/{id}) and return the updated UserDto")
    void shouldUpdateUserSuccessfully() {
        UserDto user2 = registerUser("user2", "pass2");
        String token = loginAndGetToken("user2", "pass2");

        UserUpdateDto updateDto = new UserUpdateDto("newUser2", "newPass2");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        String updateUrl = usersUrl() + "/" + user2.id();
        ResponseEntity<UserDto> updateResponse = restTemplate.exchange(
                updateUrl,
                HttpMethod.PUT,
                new HttpEntity<>(updateDto, headers),
                UserDto.class
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().username()).isEqualTo("newUser2");
    }

    @Test
    @DisplayName("Should delete a user and return ApiResponse<String>")
    void shouldDeleteUser() {
        UserDto user3 = registerUser("user3", "pass3");
        String token = loginAndGetToken("user3", "pass3");

        String deleteUrl = usersUrl() + "/" + user3.id();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<ApiResponse> deleteResponse = restTemplate.exchange(
                deleteUrl,
                HttpMethod.DELETE,
                new HttpEntity<>(null, headers),
                ApiResponse.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse body = deleteResponse.getBody();
        assertThat(body.message()).isEqualTo("User deleted successfully");
    }
}
