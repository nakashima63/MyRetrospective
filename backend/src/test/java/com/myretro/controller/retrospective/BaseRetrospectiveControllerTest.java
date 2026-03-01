package com.myretro.controller.retrospective;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myretro.entity.User;
import com.myretro.repository.RetrospectiveRepository;
import com.myretro.repository.RefreshTokenRepository;
import com.myretro.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class BaseRetrospectiveControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RetrospectiveRepository retrospectiveRepository;

    @Autowired
    protected RefreshTokenRepository refreshTokenRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected String accessToken;
    protected String otherUserAccessToken;

    @BeforeEach
    void setUp() throws Exception {
        retrospectiveRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        accessToken = createUserAndLogin("test@example.com", "password123", "testuser");
        otherUserAccessToken = createUserAndLogin("other@example.com", "password123", "otheruser");
    }

    private String createUserAndLogin(String email, String password, String username) throws Exception {
        userRepository.save(new User(email, passwordEncoder.encode(password), username));

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new com.myretro.dto.auth.LoginRequest(email, password))))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(loginResponse).get("accessToken").asText();
    }
}
