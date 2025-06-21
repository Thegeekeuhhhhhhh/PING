package fr.epita.assistants.ping.presentation.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static fr.epita.assistants.ping.errors.ErrorsCode.EXAMPLE_ERROR;
import fr.epita.assistants.ping.utils.Logger;
@Path("/api")
public class HelloWorldResource {

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public Response helloWorld() {
        Logger.logRequest("0", "/api/hello",  "all ok !!!");
        return Response.ok("Hello World !").build();
    }

    @GET
    @Path("/error")
    @Produces(MediaType.APPLICATION_JSON)
    public Response error() {
        Logger.logErrorRequest("0", "/api/hello", "error because error");
        EXAMPLE_ERROR.throwException("This is an error");
        // This line will never be reached
        return Response.noContent().build();
    }
}