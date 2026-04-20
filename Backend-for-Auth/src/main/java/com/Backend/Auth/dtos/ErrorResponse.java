package com.Backend.Auth.dtos;

import org.springframework.http.HttpStatus;

public record ErrorResponse(String message,
                            HttpStatus status
                            ){

}
