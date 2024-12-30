package org.example.crudapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.crudapi.dto.ApiResponse;
import org.example.crudapi.dto.UserCreateDto;
import org.example.crudapi.dto.UserDto;
import org.example.crudapi.dto.UserUpdateDto;
import org.example.crudapi.entity.User;
import org.example.crudapi.repository.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private BCryptPasswordEncoder encoder;
    private ObjectMapper objectMapper;

    public UserDto registerUser(UserCreateDto userCreateDto) {
        if (userRepository.existsByUsername(userCreateDto.username())){
            throw new DuplicateKeyException("Username is already in use: " + userCreateDto.username());
        }

        User user = new User();
        user.setUsername(userCreateDto.username());
        user.setPassword(encoder.encode(userCreateDto.password()));
        User saved = userRepository.saveAndFlush(user);
        return objectMapper.convertValue(saved, UserDto.class);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> objectMapper.convertValue(user, UserDto.class))
                .toList();
    }

    public ApiResponse<String> deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
        return new ApiResponse<>(HttpStatus.OK.value(), "User deleted successfully");
    }

    @Transactional
    public UserDto updateUser(Long id, UserUpdateDto userUpdateDto, Principal principal) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        String loggedInUsername = principal.getName();

        if (!user.getUsername().equals(loggedInUsername)) {
            throw new AccessDeniedException("You can only update your own profile");
        }

        if (userUpdateDto.username() != null) {
            if (userRepository.existsByUsername(userUpdateDto.username())) {
                throw new DuplicateKeyException("Username is already in use: " + userUpdateDto.username());
            }
            user.setUsername(userUpdateDto.username());
        }

        if (userUpdateDto.password() != null) {
            user.setPassword(encoder.encode(userUpdateDto.password()));
        }

        User savedUser = userRepository.saveAndFlush(user);
        return objectMapper.convertValue(savedUser, UserDto.class);
    }
}
