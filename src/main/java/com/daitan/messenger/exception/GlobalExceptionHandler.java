package com.daitan.messenger.exception;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

@ControllerAdvice
@RequestMapping(produces = "application/vnd.error+json")
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<VndErrors> userNotFoundException(final UserNotFoundException e) {
        return error(e.getErrMsg(), HttpStatus.NOT_FOUND, e.getErrCode());
    }

    private ResponseEntity<VndErrors> error(final String message, final HttpStatus httpStatus, final String logRef) {
        return new ResponseEntity<>(new VndErrors(logRef, message), httpStatus);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<VndErrors> sqlException(final UserNotFoundException e) {
        return error(e.getErrMsg(), HttpStatus.INTERNAL_SERVER_ERROR, e.getErrCode());
    }

    @ExceptionHandler(UserInvalidException.class)
    public ResponseEntity<VndErrors> sqlException(final UserInvalidException e) {
        return error(e.getErrMsg(), HttpStatus.BAD_REQUEST, e.getErrCode());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<VndErrors> sqlException(final UserAlreadyExistsException e) {
        return error(e.getErrMsg(), HttpStatus.CONFLICT, e.getErrCode());
    }
}
