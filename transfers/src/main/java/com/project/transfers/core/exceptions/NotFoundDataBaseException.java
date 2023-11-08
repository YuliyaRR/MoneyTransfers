package com.project.transfers.core.exceptions;

import com.project.transfers.core.error.ErrorCode;
import lombok.Getter;

@Getter
public class NotFoundDataBaseException extends RuntimeException {
    private final ErrorCode errorCode;

    public NotFoundDataBaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

