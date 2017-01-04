package com.flipkart.cs.languagetool.service.exception;

import lombok.Data;

/**
 * Created by anmol.kapoor on 03/01/17.
 */
@Data
public class ExceptionResponse {
    String message;
    String transactionId;
    String status;
    String cause;

    public ExceptionResponse(String message, String transactionId, String status, String cause) {
        this.message = message;
        this.transactionId = transactionId;
        this.status = status;
        this.cause = cause;
    }

    public ExceptionResponse() {
    }
}
