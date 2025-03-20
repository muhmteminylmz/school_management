package com.schoolmanagment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
//Opsiyonel ama genelde kullanilir
public class ConflictException extends RuntimeException{

    public ConflictException(String message) {
        super(message);
    }
}
