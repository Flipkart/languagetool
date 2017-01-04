package com.flipkart.cs.languagetool.service.exception;

import org.slf4j.MDC;

import javax.ws.rs.core.Response;

/**
 * Created by anmolkapoor on 18/07/14.
 */
public class ApiException extends Exception {
    private Response.Status status;
    private String message;


    public ApiException(Response.Status status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public ApiException(String message) {
        super(message);
        this.status = Response.Status.INTERNAL_SERVER_ERROR;
        this.message = message;
    }

    public ApiException(Response.Status status, String message, Throwable throwable) {
        super(message, throwable);
        this.status = status;
        this.message = message;
    }

    public ApiException(Response.Status status, Throwable throwable) {
        super(throwable.getMessage(), throwable);
        this.status = status;
        this.message = throwable.getMessage();
    }

    public Response.Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ExceptionResponse createExceptionResponse() {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setMessage(getMessage());
        exceptionResponse.setStatus(getStatus().name());
        exceptionResponse.setTransactionId(MDC.get("id"));
        if (getCause() != null) {
            exceptionResponse.setCause(getCause().getClass().getSimpleName() + " : " + getCause().getLocalizedMessage());
        }
        return exceptionResponse;
    }
}
