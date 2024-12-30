package org.example.crudapi.service;

import lombok.AllArgsConstructor;
import org.example.crudapi.entity.User;
import org.example.crudapi.entity.UserInfoDetails;
import org.example.crudapi.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username '" + username + "' not found in the database."));
        return new UserInfoDetails(user.getUsername(), user.getPassword());
    }
}
