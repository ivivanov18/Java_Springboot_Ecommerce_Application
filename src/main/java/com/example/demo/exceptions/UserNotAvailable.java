package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Username is not available")
public class UserNotAvailable extends RuntimeException {

    public UserNotAvailable() {
    }

    public UserNotAvailable(String message) {
        super(message);
    }
}



