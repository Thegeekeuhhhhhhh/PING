package fr.epita.assistants.ping.presentation.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import fr.epita.assistants.ping.domain.service.MemberService;
import fr.epita.assistants.ping.domain.service.MemberService;
import fr.epita.assistants.ping.domain.service.UserService;
import fr.epita.assistants.ping.common.api.request.UserRequest;
import fr.epita.assistants.ping.common.api.request.CreateMemberRequest;
import fr.epita.assistants.ping.common.api.request.ExecuteFeatureRequest;
import fr.epita.assistants.ping.common.api.request.LoginRequest;
import fr.epita.assistants.ping.common.api.response.UserResponse;
import fr.epita.assistants.ping.common.api.request.UpdateRequest;
import fr.epita.assistants.ping.data.model.MemberModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.common.api.response.LoginResponse;
import fr.epita.assistants.ping.common.api.response.MemberResponse;
import fr.epita.assistants.ping.common.api.response.PingMemberResponse;
import fr.epita.assistants.ping.common.api.response.MemberResponse;
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

@Path("/api/members")
public class MemberResource {
    @Inject
    Logger Logger;

    @Inject
    MemberService memberService;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/")
    // @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMembers() {
        Logger.logRequest(jwt.getSubject(), "/api/members/", "GET");

        List<PingMemberResponse> res = new ArrayList<PingMemberResponse>();
        for (MemberModel d : memberService.getMembers()) {
            res.add(new PingMemberResponse(d.login, d.name, d.role, d.status, d.id));
        }

        Logger.logRequest(jwt.getSubject(), "/api/members/", "GEt " + "all ok !!!");
        return Response.ok(res).status(200).build();
    }

    @GET
    @Path("/{id}")
    // @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMember(@PathParam("id") UUID id) {
        Logger.logRequest(jwt.getSubject(), "/api/members/{id}", "GET");

        MemberModel d = memberService.getMember(id);
        PingMemberResponse res = new PingMemberResponse(d.login, d.name, d.role, d.status, d.id);

        Logger.logRequest(jwt.getSubject(), "/api/members/{id}", "GEt " + "all ok !!!");
        return Response.ok(res).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMember(CreateMemberRequest createMemberRequest) {
        Logger.logRequest(jwt.getSubject(), "/api/members/",
                "POST\n" + createMemberRequest.login + "\n" + createMemberRequest.name + "\n" + createMemberRequest.role
                        + "\n" + createMemberRequest.status);
        MemberModel d = memberService.addMember(createMemberRequest.login, createMemberRequest.name,
                createMemberRequest.role, createMemberRequest.status);
        PingMemberResponse res = new PingMemberResponse(d.login, d.name, d.role, d.status, d.id);
        return Response.ok(res).status(200).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMember(@PathParam("id") UUID id) {
        Logger.logRequest(jwt.getSubject(), "/api/members/{id}", "DELETE");
        memberService.deleteMember(id);
        return Response.ok("SUPER !").status(204).build();
    }
}