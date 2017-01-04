package com.flipkart.cs.languagetool.service.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Created by anmolkapoor on 04/09/15.
 */
public class CustomJsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {
    private static Logger logger = LoggerFactory.getLogger(CustomJsonMappingExceptionMapper.class);

    public Response toResponse(JsonMappingException exception) {
        logger.error("There was a error in system : " + exception.getMessage(), exception);
        return Response.status(Response.Status.BAD_REQUEST).entity(new ExceptionResponse(exception.getMessage(),MDC.get("id"),Response.Status.BAD_REQUEST.name(),null)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
