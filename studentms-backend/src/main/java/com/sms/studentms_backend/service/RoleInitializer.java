package com.sms.studentms_backend.service;

import com.sms.studentms_backend.entity.Role;
import com.sms.studentms_backend.repository.RoleRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class RoleInitializer implements ApplicationRunner {

    private static final List<String> REQUIRED_ROLES =
            List.of("ROLE_STUDENT", "ROLE_ADMIN", "ROLE_TEACHER");

    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (String roleName : REQUIRED_ROLES) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
        }
    }
}
