package com.sms.studentms_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.studentms_backend.entity.Student;
import com.sms.studentms_backend.entity.User;
import com.sms.studentms_backend.repository.StudentRepository;
import com.sms.studentms_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StudentRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void registerStudentCreatesUserAndStudent() throws Exception {
        String username = uniqueValue("student");
        String email = uniqueValue("student") + "@example.com";

        String response = mockMvc.perform(post("/api/auth/register/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationPayload(username, email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Student registered successfully"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long studentId = objectMapper.readTree(response).get("studentId").asLong();
        User user = userRepository.findByUsername(username).orElseThrow();
        Student student = studentRepository.findById(studentId).orElseThrow();

        assertNotEquals("password123", user.getPassword());
        assertTrue(user.getRoles().stream().anyMatch(role -> "ROLE_STUDENT".equals(role.getName())));
        assertEquals(email, student.getEmail());
        assertEquals("Computer Science", student.getDepartment());
    }

    @Test
    void registeredStudentCanLoginAndReceiveJwt() throws Exception {
        String username = uniqueValue("student");
        String email = uniqueValue("student") + "@example.com";

        mockMvc.perform(post("/api/auth/register/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationPayload(username, email)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username,
                                "password", "password123"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.roleName").value("ROLE_STUDENT"));
    }

    @Test
    void loginWithInvalidPasswordReturnsUnauthorized() throws Exception {
        String username = uniqueValue("student");
        mockMvc.perform(post("/api/auth/register/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationPayload(username, uniqueValue("student") + "@example.com")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username,
                                "password", "wrong-password"
                        ))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password."));
    }

    @Test
    void registerStudentRejectsDuplicateUsername() throws Exception {
        String username = uniqueValue("student");
        mockMvc.perform(post("/api/auth/register/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationPayload(username, uniqueValue("first") + "@example.com")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationPayload(username, uniqueValue("second") + "@example.com")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    void registerStudentRejectsDuplicateEmail() throws Exception {
        String email = uniqueValue("student") + "@example.com";
        mockMvc.perform(post("/api/auth/register/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationPayload(uniqueValue("first"), email)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationPayload(uniqueValue("second"), email)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void registerStudentRejectsInvalidEmailAndShortPassword() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "username", uniqueValue("student"),
                "password", "short",
                "roleName", "ROLE_STUDENT",
                "name", "Test Student",
                "email", "not-an-email",
                "department", "Computer Science"
        ));

        mockMvc.perform(post("/api/auth/register/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    private String registrationPayload(String username, String email) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "username", username,
                "password", "password123",
                "roleName", "ROLE_STUDENT",
                "name", "Test Student",
                "email", email,
                "department", "Computer Science"
        ));
    }

    private String uniqueValue(String prefix) {
        return prefix + "-" + UUID.randomUUID();
    }
}
