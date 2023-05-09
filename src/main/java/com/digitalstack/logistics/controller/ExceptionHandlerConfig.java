package com.digitalstack.logistics.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class ExceptionHandlerConfig
{
    @ExceptionHandler(value = {IOException.class, NoSuchElementException.class})
    public ResponseEntity<String> handleExceptions(Exception exception) {
        if (exception instanceof IOException) {
            return new ResponseEntity<>("Error loading data from files", HttpStatus.I_AM_A_TEAPOT);
        }
        if (exception instanceof NoSuchElementException) {
            return new ResponseEntity<>("Corrupted data", HttpStatus.I_AM_A_TEAPOT);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
