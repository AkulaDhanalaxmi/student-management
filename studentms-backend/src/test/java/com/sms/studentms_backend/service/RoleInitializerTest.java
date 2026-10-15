package com.sms.studentms_backend.service;

import com.sms.studentms_backend.entity.Role;
import com.sms.studentms_backend.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleInitializerTest {

    @Mock
    private RoleRepository roleRepository;

    @Test
    void initializesRequiredRolesAndCanRunRepeatedlyWithoutDuplicates() {
        Map<String, Role> roles = new HashMap<>();
        Role existingRole = new Role();
        existingRole.setName("ROLE_ADMIN");
        roles.put(existingRole.getName(), existingRole);

        when(roleRepository.findByName(any())).thenAnswer(invocation ->
                Optional.ofNullable(roles.get(invocation.getArgument(0))));
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            roles.put(role.getName(), role);
            return role;
        });

        RoleInitializer initializer = new RoleInitializer(roleRepository);
        DefaultApplicationArguments arguments = new DefaultApplicationArguments(new String[0]);

        initializer.run(arguments);
        initializer.run(arguments);

        assertEquals(3, roles.size());
        assertEquals(
                java.util.Set.of("ROLE_STUDENT", "ROLE_ADMIN", "ROLE_TEACHER"),
                roles.keySet()
        );
        verify(roleRepository, times(2)).save(any(Role.class));
    }
}
