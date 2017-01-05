package com.flipkart.cs.languagetool.service.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.abt.rotationBundle.tasks.RotationManagementTask;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

/**
 * Created by anmol.kapoor on 03/01/17.
 */
public class ResponseFilter implements ContainerResponseFilter {
    private static final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);

    private final ObjectMapper objectMapper;
    private final RotationManagementTask rotationManagementTask;

    @Inject
    public ResponseFilter(ObjectMapper jsonHelpers, RotationManagementTask rotationManagementTask) {
        this.objectMapper = jsonHelpers;
        this.rotationManagementTask = rotationManagementTask;
    }

    private boolean isSuccessStatus(int code) {
        if (code / 200 == 1) return true;
        else if (code == 404) return true;
        else {
            return false;
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
//        RequestHeaders.clear();

        logger.info("in response filter");
        Optional<Object> responseEntity = Optional.fromNullable(responseContext.getEntity());
        int statusCode = responseContext.getStatus();
        logger.info("Response Code :{}", statusCode);
        if (!isSuccessStatus(statusCode) || (!requestContext.getMethod().toUpperCase().equals("GET"))) {
            if (responseEntity.isPresent()) {
                try {
                    logger.info("Response : {}", objectMapper.writeValueAsString(responseContext.getEntity()));
                } catch (IOException e) {
                    logger.info("Response : {}", responseContext.getEntity().toString());
                } finally {
                    responseContext.setEntity(responseEntity.get());
                }

            } else {
                logger.info("Response : Empty Response");
            }
        } else {
            logger.info("Response : Empty Response or not printing response");
        }
        responseContext.getHeaders().add("Transaction-profileId", MDC.get("profileId"));


        logger.info("Server status: " + rotationManagementTask.isActive());
        if (!rotationManagementTask.isActive()) {
            logger.info("adding header to close connection");

            responseContext.getHeaders().add("Connection", "Close");
        }
        responseContext.getHeaders().add("transactionId", MDC.get("id"));
        logger.info("done .. Returning response");


    }
}