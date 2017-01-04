package com.flipkart.cs.languagetool.service.exception;


import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Created by anmolkapoor on 18/09/14.
 */
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    final private static org.slf4j.Logger logger = LoggerFactory.getLogger(ApiExceptionMapper.class);

    @Override
    public Response toResponse(NotFoundException exception) {

        logger.info("There was not found a error in system : " + exception.getMessage());
        return Response.status(Response.Status.NOT_FOUND).entity(new ExceptionResponse(exception.getMessage(), MDC.get("id"), Response.Status.NOT_FOUND.name(), null)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
