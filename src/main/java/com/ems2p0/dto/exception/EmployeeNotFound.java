package com.ems2p0.dto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmployeeNotFound extends CustomExceptionDto{
    public EmployeeNotFound(String message) {
        super(message);
    }
}
