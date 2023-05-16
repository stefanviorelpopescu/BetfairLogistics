package com.digitalstack.logistics.controller;

import com.digitalstack.logistics.helpers.InvalidDestinationDtoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Objects;

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

    @ExceptionHandler(value = {InvalidDestinationDtoException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<String> handleDestinationAddExceptions(Exception exception) {

        if (exception instanceof MethodArgumentNotValidException) {
            Object detailMessageArgumentsRaw = Objects.requireNonNull(((MethodArgumentNotValidException) exception).getDetailMessageArguments())[1];
            if (detailMessageArgumentsRaw instanceof ArrayList) {
                ArrayList<?> detailMessageArguments = (ArrayList<?>) Objects.requireNonNull(detailMessageArgumentsRaw);
                StringBuilder errorMessage = new StringBuilder();
                for (Object detailMessageArgument : detailMessageArguments)
                {
                    errorMessage.append(detailMessageArgument);
                    errorMessage.append("\n");
                }
                return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
