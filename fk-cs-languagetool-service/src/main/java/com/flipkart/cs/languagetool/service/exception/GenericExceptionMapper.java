package com.flipkart.cs.languagetool.service.exception;


import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Created by anmolkapoor on 29/07/14.
 */
public class GenericExceptionMapper implements ExceptionMapper<Exception> {
    final private static org.slf4j.Logger logger = LoggerFactory.getLogger(ApiExceptionMapper.class);
    @Override
    public Response toResponse(Exception exception) {
        logger.error("There was unknown a error in system : " + exception.getMessage(), exception);
        String cause = null;
        if(exception.getCause()!=null)
        {
            cause = exception.getClass().getSimpleName()+":"+exception.getCause().getLocalizedMessage();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ExceptionResponse(exception.getMessage(),MDC.get("id"),Response.Status.INTERNAL_SERVER_ERROR.name(),cause)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
