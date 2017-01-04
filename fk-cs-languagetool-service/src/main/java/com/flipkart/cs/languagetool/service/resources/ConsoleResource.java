package com.flipkart.cs.languagetool.service.resources;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by anmol.kapoor on 02/01/17.
 */
@Slf4j
@Path("/console")
@Produces(MediaType.APPLICATION_JSON)
public class ConsoleResource {
    @Inject
    public ConsoleResource() {
    }

    @GET
    @Path("/someConsoleApi")
    public Response someConsoleApi()
    {
        return Response.ok("someConsoleApi").build();
    }
}
