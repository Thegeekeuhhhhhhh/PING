package fr.epita.assistants.ping.presentation.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import fr.epita.assistants.ping.domain.service.DangerService;
import fr.epita.assistants.ping.domain.service.DangerService;
import fr.epita.assistants.ping.domain.service.UserService;
import fr.epita.assistants.ping.common.api.request.UserRequest;
import fr.epita.assistants.ping.common.api.request.CreateDangerRequest;
import fr.epita.assistants.ping.common.api.request.ExecuteFeatureRequest;
import fr.epita.assistants.ping.common.api.request.LoginRequest;
import fr.epita.assistants.ping.common.api.response.UserResponse;
import fr.epita.assistants.ping.common.api.request.UpdateRequest;
import fr.epita.assistants.ping.data.model.DangerModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.common.api.response.LoginResponse;
import fr.epita.assistants.ping.common.api.response.MemberResponse;
import fr.epita.assistants.ping.common.api.response.DangerResponse;
import fr.epita.assistants.ping.common.api.response.SimpleMessageResponse;
import fr.epita.assistants.ping.utils.ErrorInfo;
import io.quarkus.security.Authenticated;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtSignature;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;

import java.util.*;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.io.File;
import java.time.*;

import fr.epita.assistants.ping.utils.Logger;

@Path("/api/dangers")
public class DangerResource {
    @Inject
    Logger Logger;

    @Inject
    DangerService dangerService;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/")
    // @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDangers() {
        Logger.logRequest(jwt.getSubject(), "/api/dangers/", "GET");

        List<DangerResponse> res = new ArrayList<DangerResponse>();
        for (DangerModel d : dangerService.getDangers()) {
            res.add(new DangerResponse(d.place, d.number, d.type, d.description));
        }

        Logger.logRequest(jwt.getSubject(), "/api/dangers/", "GEt " + "all ok !!!");
        return Response.ok(res).status(200).build();
    }

    @GET
    @Path("/{id}")
    // @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDanger(@PathParam("id") UUID id) {
        Logger.logRequest(jwt.getSubject(), "/api/dangers/{id}", "GET");

        DangerModel d = dangerService.getDanger(id);
        DangerResponse res = new DangerResponse(d.place, d.number, d.type, d.description);

        Logger.logRequest(jwt.getSubject(), "/api/dangers/{id}", "GEt " + "all ok !!!");
        return Response.ok(res).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createDanger(CreateDangerRequest createDangerRequest) {
        Logger.logRequest(jwt.getSubject(), "/api/dangers/",
                "POST\n" + "Endroit: " + createDangerRequest.number + " " + createDangerRequest.place + "\nDanger: "
                        + createDangerRequest.type + "\nDescription: " + createDangerRequest.description);
        DangerModel d = dangerService.addDanger(createDangerRequest.place, createDangerRequest.number,
                createDangerRequest.type, createDangerRequest.description);
        DangerResponse res = new DangerResponse(d.place, d.number, d.type, d.description);
        return Response.ok(res).status(200).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDanger(@PathParam("id") UUID id) {
        Logger.logRequest(jwt.getSubject(), "/api/dangers/{id}", "DELETE");
        dangerService.deleteDanger(id);
        return Response.ok("SUPER !").status(204).build();
    }

}