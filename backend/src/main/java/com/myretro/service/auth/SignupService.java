package com.myretro.service.auth;

import com.myretro.entity.User;
import com.myretro.exception.EmailAlreadyExistsException;
import com.myretro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User signup(String email, String password, String username) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(email, hashedPassword, username);
        return userRepository.save(user);
    }
}
