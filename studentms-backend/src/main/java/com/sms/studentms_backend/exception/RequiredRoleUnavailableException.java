package com.sms.studentms_backend.exception;

public class RequiredRoleUnavailableException extends RuntimeException {
    public RequiredRoleUnavailableException() {
        super("A required application role is unavailable");
    }
}
