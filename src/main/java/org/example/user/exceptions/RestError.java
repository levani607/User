package org.example.user.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RestError {

    private String exceptionId;
    private String message;
}
