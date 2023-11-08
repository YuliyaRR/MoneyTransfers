package com.project.transfers.core.exceptions;

import com.project.transfers.core.error.ErrorCode;
import lombok.Getter;

@Getter
public class ConversionTimeException extends RuntimeException {
    private final ErrorCode errorCode;

    public ConversionTimeException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

