package fr.epita.assistants.ping.presentation.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import fr.epita.assistants.ping.common.api.request.UserRequest;
import fr.epita.assistants.ping.utils.ErrorInfo;

@Path("/api")
public class UserResource {
    @GET
    @Path("/user")
    @Produces(MediaType.TEXT_PLAIN)
    public Response user(UserRequest userRequest) {
        if (!userRequest.isAdmin) {
            return Response.ok(new ErrorInfo("Caca et pipi sont sur un bateau")).status(403).build();
        }
        return Response.ok("Hello World !").build();
    }
}