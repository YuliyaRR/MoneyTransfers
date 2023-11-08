package com.project.transfers.core.exceptions;

import com.project.transfers.core.error.ErrorCode;
import lombok.Getter;

public class InvalidInputServiceMultiException extends RuntimeException{
    private String field;
    @Getter
    private ErrorCode errorCode;

    public InvalidInputServiceMultiException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public InvalidInputServiceMultiException(String s, String field) {
        super(s);
        this.field = field;
    }

    @Override
    public String getLocalizedMessage() {
        return field;
    }
}
