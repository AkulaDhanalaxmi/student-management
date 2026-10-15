package com.sms.studentms_backend.service;

import com.sms.studentms_backend.dto.StudentRegistrationRequest;
import com.sms.studentms_backend.exception.RequiredRoleUnavailableException;
import com.sms.studentms_backend.repository.RoleRepository;
import com.sms.studentms_backend.repository.StudentRepository;
import com.sms.studentms_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudentRegistrationService studentRegistrationService;

    @Test
    void registerWhenStudentRoleIsMissingFailsBeforeCreatingAccountRecords() {
        StudentRegistrationRequest request = new StudentRegistrationRequest();
        request.setUsername("new-student");
        request.setPassword("password123");
        request.setEmail("student@example.com");
        request.setName("New Student");
        request.setDepartment("Computer Science");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(studentRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.empty());

        assertThrows(RequiredRoleUnavailableException.class,
                () -> studentRegistrationService.register(request));

        verify(userRepository, never()).save(any());
        verify(studentRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }
}
