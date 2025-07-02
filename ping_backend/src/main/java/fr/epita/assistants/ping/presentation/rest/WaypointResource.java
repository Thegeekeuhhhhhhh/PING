package fr.epita.assistants.ping.presentation.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import fr.epita.assistants.ping.domain.service.WaypointService;
import fr.epita.assistants.ping.domain.service.WaypointService;
import fr.epita.assistants.ping.domain.service.UserService;
import fr.epita.assistants.ping.common.api.request.UserRequest;
import fr.epita.assistants.ping.common.api.request.CreateWaypointRequest;
import fr.epita.assistants.ping.common.api.request.ExecuteFeatureRequest;
import fr.epita.assistants.ping.common.api.request.LoginRequest;
import fr.epita.assistants.ping.common.api.response.UserResponse;
import fr.epita.assistants.ping.common.api.request.UpdateRequest;
import fr.epita.assistants.ping.data.model.WaypointModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.common.api.response.LoginResponse;
import fr.epita.assistants.ping.common.api.response.WaypointResponse;
import fr.epita.assistants.ping.common.api.response.WaypointResponse;
import fr.epita.assistants.ping.common.api.response.WaypointResponse;
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

@Path("/api/waypoints")
public class WaypointResource {
    @Inject
    Logger Logger;

    @Inject
    WaypointService waypointService;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/")
    // @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWaypoints() {
        Logger.logRequest(jwt.getSubject(), "/api/waypoints/", "GET");

        List<WaypointResponse> res = new ArrayList<WaypointResponse>();
        for (WaypointModel d : waypointService.getWaypoints()) {
            res.add(new WaypointResponse(d.name, d.lat, d.lng, d.order, d.completed, d.id));
        }

        Logger.logRequest(jwt.getSubject(), "/api/waypoints/", "GEt " + "all ok !!!");
        return Response.ok(res).status(200).build();
    }

    @GET
    @Path("/{id}")
    // @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWaypoint(@PathParam("id") UUID id) {
        Logger.logRequest(jwt.getSubject(), "/api/waypoints/{id}", "GET");

        WaypointModel d = waypointService.getWaypoint(id);
        WaypointResponse res = new WaypointResponse(d.name, d.lat, d.lng, d.order, d.completed, d.id);

        Logger.logRequest(jwt.getSubject(), "/api/waypoints/{id}", "GEt " + "all ok !!!");
        return Response.ok(res).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createWaypoint(CreateWaypointRequest createWaypointRequest) {
        Logger.logRequest(jwt.getSubject(), "/api/waypoints/",
                "POST\n" + createWaypointRequest.name + "\n" + createWaypointRequest.lat + " "
                        + createWaypointRequest.lng
                        + createWaypointRequest.order
                        + "\n" + createWaypointRequest.completed);
        WaypointModel d = waypointService.addWaypoint(createWaypointRequest.name, createWaypointRequest.lat,
                createWaypointRequest.lng, createWaypointRequest.order, createWaypointRequest.completed);
        WaypointResponse res = new WaypointResponse(d.name, d.lat, d.lng, d.order, d.completed, d.id);
        return Response.ok(res).status(200).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteWaypoint(@PathParam("id") UUID id) {
        Logger.logRequest(jwt.getSubject(), "/api/waypoints/{id}", "DELETE");
        waypointService.deleteWaypoint(id);
        return Response.ok("SUPER !").status(204).build();
    }
}