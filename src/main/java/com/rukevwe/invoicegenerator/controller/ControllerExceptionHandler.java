package com.rukevwe.invoicegenerator.controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler {
    
    @ResponseStatus( HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ParseException.class)
    public Map<String, String> handleParseException(ParseException e) {
       return Collections.singletonMap("errorMessage", e.getMessage() + " at line" + e.getErrorOffset());
    }
    
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IOException.class)
    public Map<String, String> handleIOException(IOException e) {
        return Collections.singletonMap("errorMessage", "Error parsing CSV file");

    }
}
