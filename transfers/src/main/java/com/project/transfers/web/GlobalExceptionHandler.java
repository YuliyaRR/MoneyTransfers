package com.project.transfers.web;

import com.project.transfers.core.error.ErrorCode;
import com.project.transfers.core.error.LocalError;
import com.project.transfers.core.error.ResponseMultiError;
import com.project.transfers.core.error.ResponseSingleError;
import com.project.transfers.core.exceptions.ConversionTimeException;
import com.project.transfers.core.exceptions.InvalidInputServiceMultiException;
import com.project.transfers.core.exceptions.InvalidInputServiceSingleException;
import com.project.transfers.core.exceptions.NotFoundDataBaseException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ResponseMultiError> handle(InvalidInputServiceMultiException e) {
        Throwable[] suppressed = e.getSuppressed();
        List<LocalError> localErrors = Arrays.stream(suppressed)
                .map(thr -> new LocalError(thr.getLocalizedMessage(), thr.getMessage()))
                .toList();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMultiError(e.getErrorCode(), localErrors));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseMultiError> handle(MethodArgumentNotValidException e) {
        List<LocalError> localErrors = e.getBindingResult().getAllErrors()
                .stream().map(error -> new LocalError(((FieldError) error).getField(), error.getDefaultMessage())).toList();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMultiError(ErrorCode.STRUCTURED_ERROR, localErrors));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseMultiError> handle(ConstraintViolationException e) {
        List<LocalError> localErrors = new ArrayList<>();
        for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
            String name = null;
            for (Path.Node node : constraintViolation.getPropertyPath()) {
                name = node.getName();
            }
            localErrors.add(new LocalError(name, constraintViolation.getMessage()));
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMultiError(ErrorCode.STRUCTURED_ERROR, localErrors));
    }

    @ExceptionHandler
    public ResponseEntity<List<ResponseSingleError>> handle(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(List.of(new ResponseSingleError(ErrorCode.ERROR, e.getMessage())));
    }

    @ExceptionHandler
    public ResponseEntity<List<ResponseSingleError>> handle(InvalidInputServiceSingleException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(List.of(new ResponseSingleError(e.getErrorCode(), e.getMessage())));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseSingleError> handle(NotFoundDataBaseException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponseSingleError(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(value = {ConversionTimeException.class, Exception.class})
    public ResponseEntity<List<ResponseSingleError>> handle(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(List.of(new ResponseSingleError(ErrorCode.ERROR, e.getMessage())));
    }

}
