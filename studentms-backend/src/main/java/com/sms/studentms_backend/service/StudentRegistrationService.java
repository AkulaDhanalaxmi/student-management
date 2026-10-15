package com.sms.studentms_backend.service;

import com.sms.studentms_backend.dto.StudentRegistrationRequest;
import com.sms.studentms_backend.entity.Role;
import com.sms.studentms_backend.entity.Student;
import com.sms.studentms_backend.entity.User;
import com.sms.studentms_backend.exception.AccountAlreadyExistsException;
import com.sms.studentms_backend.exception.RequiredRoleUnavailableException;
import com.sms.studentms_backend.repository.RoleRepository;
import com.sms.studentms_backend.repository.StudentRepository;
import com.sms.studentms_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class StudentRegistrationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentRegistrationService(UserRepository userRepository,
                                      RoleRepository roleRepository,
                                      StudentRepository studentRepository,
                                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Student register(StudentRegistrationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AccountAlreadyExistsException("Username already exists");
        }
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new AccountAlreadyExistsException("Email already exists");
        }

        Role role = roleRepository.findByName("ROLE_STUDENT")
                .orElseThrow(RequiredRoleUnavailableException::new);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(role));
        userRepository.save(user);

        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setDepartment(request.getDepartment());
        student.setDeleted(false);
        return studentRepository.save(student);
    }
}
