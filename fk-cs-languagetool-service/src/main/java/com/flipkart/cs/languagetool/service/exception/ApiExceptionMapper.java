package com.flipkart.cs.languagetool.service.exception;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Created by anmolkapoor on 29/07/14.
 */
public class ApiExceptionMapper implements ExceptionMapper<ApiException> {
    final private static org.slf4j.Logger logger = LoggerFactory.getLogger(ApiExceptionMapper.class);

    @Override
    public Response toResponse(ApiException exception) {

        logger.error("There was a error in system : " + exception.getMessage(), exception);
        return Response.status(exception.getStatus()).entity(exception.createExceptionResponse()).type(MediaType.APPLICATION_JSON_TYPE).build();

    }
}
