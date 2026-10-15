package com.sms.studentms_backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    @Test
    void requiredRoleUnavailableReturnsSafeServiceUnavailableResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/register/student");

        ResponseEntity<ErrorResponse> response =
                handler.handleRequiredRoleUnavailable(new RequiredRoleUnavailableException(), request);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(
                "Registration is temporarily unavailable. Please try again later.",
                response.getBody().getMessage()
        );
        assertEquals("/api/auth/register/student", response.getBody().getPath());
    }
}
