package com.myretro.controller.kptitem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myretro.entity.Retrospective;
import com.myretro.entity.User;
import com.myretro.repository.KptItemRepository;
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
abstract class BaseKptItemControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RetrospectiveRepository retrospectiveRepository;

    @Autowired
    protected KptItemRepository kptItemRepository;

    @Autowired
    protected RefreshTokenRepository refreshTokenRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected String accessToken;
    protected Retrospective retrospective;

    @BeforeEach
    void setUp() throws Exception {
        kptItemRepository.deleteAll();
        retrospectiveRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        User user = userRepository.save(
                new User("test@example.com", passwordEncoder.encode("password123"), "testuser"));

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new com.myretro.dto.auth.LoginRequest("test@example.com", "password123"))))
                .andReturn().getResponse().getContentAsString();

        accessToken = objectMapper.readTree(loginResponse).get("accessToken").asText();
        retrospective = retrospectiveRepository.save(new Retrospective(user, "Sprint 1", "desc"));
    }
}
