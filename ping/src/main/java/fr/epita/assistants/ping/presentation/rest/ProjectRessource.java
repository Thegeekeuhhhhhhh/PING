package fr.epita.assistants.ping.presentation.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import fr.epita.assistants.ping.domain.service.ProjectService;
import fr.epita.assistants.ping.domain.service.UserService;
import fr.epita.assistants.ping.common.api.request.UserRequest;
import fr.epita.assistants.ping.common.api.request.LoginRequest;
import fr.epita.assistants.ping.common.api.request.ProjectRequest;
import fr.epita.assistants.ping.common.api.response.UserResponse;
import fr.epita.assistants.ping.common.api.request.UpdateRequest;
import fr.epita.assistants.ping.data.model.ProjectModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.common.api.response.LoginResponse;
import fr.epita.assistants.ping.common.api.response.MemberResponse;
import fr.epita.assistants.ping.common.api.response.ProjectResponse;
import fr.epita.assistants.ping.utils.ErrorInfo;
import io.quarkus.security.Authenticated;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtSignature;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;

import java.util.*;

import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.*;

@Path("/api")
public class ProjectRessource {

    @Inject
    ProjectService projectService;

    @Inject
    UserService userService;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/projects")
    @Authenticated
    @Produces(MediaType.APPLICATION_JSON)
    public Response userProjects() {
        UUID id = UUID.fromString(jwt.getSubject());
        ArrayList<ProjectModel> list = new ArrayList<ProjectModel>(projectService.GetUserProjects(id));
        ArrayList<ProjectResponse> response = new ArrayList<ProjectResponse>();
        for (ProjectModel pm : list) {
            ArrayList<MemberResponse> mr = new ArrayList<MemberResponse>();
            for (UserModel um : pm.members) {
                mr.add(new MemberResponse(um.id, um.displayName, um.avatar));
            }
            response.add(new ProjectResponse(pm.name, mr,
                    new MemberResponse(pm.owner.id, pm.owner.displayName, pm.owner.avatar)));
        }
        return Response.ok(response).status(200).build();
    }

    @POST
    @Path("/projects")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProjects(ProjectRequest request) {
        UUID id = UUID.fromString(jwt.getSubject());
        UserModel owner = userService.GetUser(id);

        ProjectModel project = projectService.AddProject(request.getName(), owner);

        ArrayList<MemberResponse> mr = new ArrayList<MemberResponse>();

        for (UserModel um : project.members) {
            mr.add(new MemberResponse(um.id, um.displayName, um.avatar));
        }

        return Response.ok(new ProjectResponse(project.name, mr,
                new MemberResponse(project.owner.id, project.owner.displayName, project.owner.avatar))).status(200)
                .build();
    }

    @GET
    @Path("/projects/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response allProjects() {
        ArrayList<ProjectModel> list = new ArrayList<ProjectModel>(projectService.GetProjects());
        ArrayList<ProjectResponse> response = new ArrayList<ProjectResponse>();
        for (ProjectModel pm : list) {
            ArrayList<MemberResponse> mr = new ArrayList<MemberResponse>();
            for (UserModel um : pm.members) {
                mr.add(new MemberResponse(um.id, um.displayName, um.avatar));
            }
            response.add(new ProjectResponse(pm.name, mr,
                    new MemberResponse(pm.owner.id, pm.owner.displayName, pm.owner.avatar)));
        }
        return Response.ok(response).status(200).build();
    }
}