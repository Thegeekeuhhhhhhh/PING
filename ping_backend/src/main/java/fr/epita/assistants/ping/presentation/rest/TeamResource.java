package fr.epita.assistants.ping.presentation.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import fr.epita.assistants.ping.domain.service.TeamService;
import fr.epita.assistants.ping.domain.service.TeamService;
import fr.epita.assistants.ping.domain.service.UserService;
import fr.epita.assistants.ping.common.api.request.UserRequest;
import fr.epita.assistants.ping.common.api.request.CreateTeamRequest;
import fr.epita.assistants.ping.common.api.request.ExecuteFeatureRequest;
import fr.epita.assistants.ping.common.api.request.LoginRequest;
import fr.epita.assistants.ping.common.api.response.UserResponse;
import fr.epita.assistants.ping.common.api.response.WaypointResponse;
import fr.epita.assistants.ping.common.api.request.UpdateRequest;
import fr.epita.assistants.ping.data.model.MemberModel;
import fr.epita.assistants.ping.data.model.TeamModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.data.model.WaypointModel;
import fr.epita.assistants.ping.common.api.response.LoginResponse;
import fr.epita.assistants.ping.common.api.response.PingMemberResponse;
import fr.epita.assistants.ping.common.api.response.TeamResponse;
import fr.epita.assistants.ping.common.api.response.TeamResponse;
import fr.epita.assistants.ping.common.api.response.TeamResponse;
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

@Path("/api/teams")
public class TeamResource {
    @Inject
    Logger Logger;

    @Inject
    TeamService teamService;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/")
    // @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeams() {
        Logger.logRequest(jwt.getSubject(), "/api/teams/", "GET");

        List<TeamResponse> res = new ArrayList<TeamResponse>();
        for (TeamModel d : teamService.getTeams()) {
            List<PingMemberResponse> onrefaittout = new ArrayList<PingMemberResponse>();
            for (MemberModel m : d.members) {
                onrefaittout.add(new PingMemberResponse(m.login, m.name, m.role, m.status, m.id));
            }
            List<WaypointResponse> waouh = new ArrayList<WaypointResponse>();
            for (WaypointModel w : d.waypoints) {
                waouh.add(new WaypointResponse(w.name, w.lat, w.lng, w.order, w.completed, w.id));
            }
            res.add(new TeamResponse(d.name, d.color, d.status, onrefaittout, waouh, d.id));
        }

        Logger.logRequest(jwt.getSubject(), "/api/teams/", "GET " + "all ok !!!");
        return Response.ok(res).status(200).build();
    }

    @GET
    @Path("/{id}")
    // @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeam(@PathParam("id") UUID id) {
        Logger.logRequest(jwt.getSubject(), "/api/teams/{id}", "GET");

        TeamModel d = teamService.getTeam(id);

        List<PingMemberResponse> onrefaittout = new ArrayList<PingMemberResponse>();
        for (MemberModel m : d.members) {
            onrefaittout.add(new PingMemberResponse(m.login, m.name, m.role, m.status, m.id));
        }
        List<WaypointResponse> waouh = new ArrayList<WaypointResponse>();
        for (WaypointModel w : d.waypoints) {
            waouh.add(new WaypointResponse(w.name, w.lat, w.lng, w.order, w.completed, w.id));
        }
        TeamResponse res = new TeamResponse(d.name, d.color, d.status, onrefaittout, waouh, d.id);

        Logger.logRequest(jwt.getSubject(), "/api/teams/{id}", "GET " + "all ok !!!");
        return Response.ok(res).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTeam(CreateTeamRequest createTeamRequest) {
        String mdr = "";
        for (UUID t : createTeamRequest.lm) {
            mdr += t.toString() + "\n";
        }
        String lol = "";
        for (UUID t : createTeamRequest.lw) {
            lol += t.toString() + "\n";
        }
        Logger.logRequest(jwt.getSubject(), "/api/teams/",
                "POST\n" + createTeamRequest.name + "\n" + createTeamRequest.color + "\n" + createTeamRequest.status
                        + "\n" + mdr);
        TeamModel d = teamService.addTeam(createTeamRequest.name, createTeamRequest.color, createTeamRequest.status,
                createTeamRequest.lm, createTeamRequest.lw);

        List<PingMemberResponse> onrefaittout = new ArrayList<PingMemberResponse>();
        for (MemberModel m : d.members) {
            onrefaittout.add(new PingMemberResponse(m.login, m.name, m.role, m.status, m.id));
        }
        List<WaypointResponse> waouh = new ArrayList<WaypointResponse>();
        for (WaypointModel w : d.waypoints) {
            waouh.add(new WaypointResponse(w.name, w.lat, w.lng, w.order, w.completed, w.id));
        }
        TeamResponse res = new TeamResponse(d.name, d.color, d.status, onrefaittout, waouh, d.id);

        return Response.ok(res).status(200).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTeam(@PathParam("id") UUID id) {
        Logger.logRequest(jwt.getSubject(), "/api/teams/{id}", "DELETE");
        teamService.deleteTeam(id);
        return Response.ok("SUPER !").status(204).build();
    }
}