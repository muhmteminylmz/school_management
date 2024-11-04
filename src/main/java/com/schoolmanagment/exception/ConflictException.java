package com.schoolmanagment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)//Bu custom exception o yuzden kendimiz status code atayabiliyoruz.
//Opsiyonel ama genelde kullanilir
public class ConflictException extends RuntimeException{

    public ConflictException(String message) {
        super(message);
    }
}
