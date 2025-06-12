package fr.epita.assistants.ping.presentation.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import fr.epita.assistants.ping.common.api.request.UserRequest;
import fr.epita.assistants.ping.common.api.response.UserResponse;
import fr.epita.assistants.ping.utils.ErrorInfo;

@Path("/api")
public class UserResource {
    @POST
    @Path("/user")
    @Produces(MediaType.TEXT_PLAIN)
    public Response user(UserRequest userRequest) {
        if (userRequest == null) {
            return Response.ok(new ErrorInfo("Caca et pipi sont sur un bateau")).status(401).build();
        }
        // Je sais pas a quel moment renvoyer 401 ?
        if (!userRequest.isAdmin) {
            return Response.ok(new ErrorInfo("Caca et pipi sont sur un bateau")).status(403).build();
        }
        boolean legit = false;
        String first = "";
        String last = "";
        for (char c : userRequest.login.toCharArray()) {
            if (c == '.' || c == '_') {
                if (legit) {
                    // On est deja venus
                    return Response.ok(new ErrorInfo("NAN GROS NAN YUKI")).status(400).build();
                }
                legit = true;
            } else {
                if (!legit) {
                    first += c;
                } else {
                    last += c;
                }
            }
        }
        if (!legit) {
            return Response.ok(new ErrorInfo("NAN GROS NAN YUKI")).status(400).build();
        }

        // TODO
        // Envoyer le user a la database
        // Si ca existe deja -> 409

        first = first.substring(0, 1).toUpperCase() + first.substring(1);
        last = last.substring(0, 1).toUpperCase() + last.substring(1);
        String completeName = first + " " + last;

        // On recupere les 3 prochains fields dans la database
        String idea = "ID";
        Boolean adminoupas = false;
        String avatar = "";
        return Response.ok(new UserResponse(idea, userRequest.login, completeName, adminoupas, avatar)).status(200)
                .build();
    }
}