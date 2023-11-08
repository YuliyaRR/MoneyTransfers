package com.project.transfers.core.exceptions;

import com.project.transfers.core.error.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidInputServiceSingleException extends RuntimeException{
    private final ErrorCode errorCode;

    public InvalidInputServiceSingleException(String s, ErrorCode errorCode) {
        super(s);
        this.errorCode = errorCode;
    }
}
