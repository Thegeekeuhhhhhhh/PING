package fr.epita.assistants.ping.presentation.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import fr.epita.assistants.ping.domain.service.ProjectService;
import fr.epita.assistants.ping.domain.service.UserService;
import fr.epita.assistants.ping.common.api.request.UserRequest;
import fr.epita.assistants.ping.common.api.request.AddMemberToProjectRequest;
import fr.epita.assistants.ping.common.api.request.ExecuteFeatureRequest;
import fr.epita.assistants.ping.common.api.request.LoginRequest;
import fr.epita.assistants.ping.common.api.request.ProjectRequest;
import fr.epita.assistants.ping.common.api.request.UpdateProjectRequest;
import fr.epita.assistants.ping.common.api.response.UserResponse;
import fr.epita.assistants.ping.common.api.request.UpdateRequest;
import fr.epita.assistants.ping.data.model.ProjectModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.common.api.response.LoginResponse;
import fr.epita.assistants.ping.common.api.response.MemberResponse;
import fr.epita.assistants.ping.common.api.response.ProjectResponse;
import fr.epita.assistants.ping.common.api.response.SimpleMessageResponse;
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

@Path("/api/projects")
public class ProjectRessource {

    @Inject
    ProjectService projectService;

    @Inject
    UserService userService;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/")
    @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_JSON)
    public Response userProjects(@QueryParam("onlyOwned") Boolean onlyOwned) {
        UUID id = UUID.fromString(jwt.getSubject());
        ArrayList<ProjectResponse> response = new ArrayList<ProjectResponse>();

        for (ProjectModel pm : projectService.GetUserProjects(id)) {
            ArrayList<MemberResponse> mr = new ArrayList<MemberResponse>();

            for (UserModel um : pm.members) {
                mr.add(new MemberResponse(um.id, um.displayName, um.avatar));
            }
            if (onlyOwned) {
                if (id.equals(pm.owner.id)) {
                    response.add(new ProjectResponse(pm.name, mr,
                            new MemberResponse(pm.owner.id, pm.owner.displayName, pm.owner.avatar)));
                }
            } else {
                response.add(new ProjectResponse(pm.name, mr,
                        new MemberResponse(pm.owner.id, pm.owner.displayName, pm.owner.avatar)));
            }
        }
        return Response.ok(response).status(200).build();
    }

    @POST
    @Path("/")
    @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProjects(ProjectRequest request) {
        UUID id = UUID.fromString(jwt.getSubject());
        UserModel owner = userService.GetUser(id);

        ProjectModel project = projectService.addProject(request.name, owner);

        ArrayList<MemberResponse> mr = new ArrayList<MemberResponse>();

        for (UserModel um : project.members) {
            mr.add(new MemberResponse(um.id, um.displayName, um.avatar));
        }

        return Response.ok(new ProjectResponse(project.name, mr,
                new MemberResponse(project.owner.id, project.owner.displayName, project.owner.avatar))).status(200)
                .build();
    }

    @GET
    @Path("/all")
    @RolesAllowed({ "admin" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response allProjects() {
        ArrayList<ProjectResponse> response = new ArrayList<ProjectResponse>();

        for (ProjectModel pm : projectService.GetProjects()) {
            ArrayList<MemberResponse> mr = new ArrayList<MemberResponse>();
            for (UserModel um : pm.members) {
                mr.add(new MemberResponse(um.id, um.displayName, um.avatar));
            }
            response.add(new ProjectResponse(pm.name, mr,
                    new MemberResponse(pm.owner.id, pm.owner.displayName, pm.owner.avatar)));
        }
        return Response.ok(response).status(200).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProject(@PathParam("id") UUID id, UpdateProjectRequest updateProjectRequest) {
        if (updateProjectRequest == null
                || (updateProjectRequest.name == null && updateProjectRequest.newOwnerId == null)) {
            return Response.ok(new ErrorInfo("Nan la c'est abuse en vrai"))
                    .status(404).build();
        }

        String name = updateProjectRequest.name;
        UUID newId = updateProjectRequest.newOwnerId;
        if (name == null) {
            name = "";
        }

        String grp = "";
        for (String tmp : jwt.getGroups()) {
            grp = tmp;
            break;
        }

        if (grp.equals("user")) {
            String idstr = jwt.getSubject();
            UUID realId = UUID.fromString(idstr);
            Boolean ok = false;
            for (ProjectModel temp : projectService.getProjects()) {
                if (temp.owner.id.equals(realId)) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(403).build();
            }
        }

        ProjectModel p = projectService.updateProject(name, id, newId);
        if (p == null) {
            return Response.ok(new ErrorInfo("ARRETE Nan la c'est abuse en vrai"))
                    .status(404).build();
        }

        ArrayList<MemberResponse> mr = new ArrayList<MemberResponse>();

        for (UserModel um : p.members) {
            mr.add(new MemberResponse(um.id, um.displayName, um.avatar));
        }

        return Response.ok(new ProjectResponse(p.name, mr,
                new MemberResponse(p.owner.id, p.owner.displayName, p.owner.avatar))).status(200)
                .build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProject(@PathParam("id") UUID id) {
        String grp = "";
        for (String tmp : jwt.getGroups()) {
            grp = tmp;
            break;
        }

        if (grp.equals("user")) {
            String idstr = jwt.getSubject();
            UUID realId = UUID.fromString(idstr);
            Boolean ok = false;
            for (ProjectModel temp : projectService.getProjects()) {
                if (temp.owner.id.equals(realId)) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(403).build();
            }
        }

        ProjectModel project = projectService.getProject(id);
        if (project == null) {
            return Response.ok(new ErrorInfo("Y'a rien la")).status(404).build();
        }

        ArrayList<MemberResponse> mr = new ArrayList<MemberResponse>();

        for (UserModel um : project.members) {
            mr.add(new MemberResponse(um.id, um.displayName, um.avatar));
        }

        return Response.ok(new ProjectResponse(project.name, mr,
                new MemberResponse(project.owner.id, project.owner.displayName, project.owner.avatar))).status(200)
                .build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteProject(@PathParam("id") UUID id) {
        String grp = "";
        for (String tmp : jwt.getGroups()) {
            grp = tmp;
            break;
        }

        if (grp.equals("user")) {
            String idstr = jwt.getSubject();
            UUID realId = UUID.fromString(idstr);
            Boolean ok = false;
            for (ProjectModel temp : projectService.getProjects()) {
                for (UserModel u : temp.members) {
                    if (u.id.equals(realId)) {
                        ok = true;
                        break;
                    }
                }
            }
            if (!ok) {
                return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(403).build();
            }
        }

        ProjectModel project = projectService.getProject(id);
        if (project == null) {
            return Response.ok(new ErrorInfo("Y'a rien la")).status(404).build();
        }

        projectService.deleteProject(id);

        return Response.ok(new SimpleMessageResponse("WOW")).status(204).build();
    }

    @POST
    @Path("/{id}/add-user")
    @RolesAllowed({ "admin", "user" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMember(@PathParam("id") UUID id, AddMemberToProjectRequest addMemberToProjectRequest) {
        if (addMemberToProjectRequest == null || addMemberToProjectRequest.userId == null) {
            return Response.ok(new ErrorInfo("ARRETE")).status(400).build();
        }

        String grp = "";
        for (String tmp : jwt.getGroups()) {
            grp = tmp;
            break;
        }

        if (grp.equals("user")) {
            String idstr = jwt.getSubject();
            UUID realId = UUID.fromString(idstr);
            Boolean ok = false;
            for (ProjectModel temp : projectService.getProjects()) {
                for (UserModel u : temp.members) {
                    if (u.id.equals(realId)) {
                        ok = true;
                        break;
                    }
                }
            }
            if (!ok) {
                return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(403).build();
            }
        }

        ProjectModel project = projectService.getProject(id);
        UserModel newMember = userService.GetUser(addMemberToProjectRequest.userId);
        if (project == null || newMember == null) {
            return Response.ok(new ErrorInfo("Je t'ai pas trouve gros...")).status(404).build();
        }

        for (UserModel user : project.members) {
            if (user.id.equals(newMember.id)) {
                return Response.ok(new ErrorInfo("T'es deja dans le projet faut suivre un peu...")).status(409).build();
            }
        }

        project.members.add(newMember);

        return Response.ok(new SimpleMessageResponse("Yo la team")).status(204).build();
    }

    @POST
    @Path("/{id}/exec")
    @RolesAllowed({ "admin", "user" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response executeFeature(@PathParam("id") UUID id, ExecuteFeatureRequest executeFeatureRequest) {
        if (executeFeatureRequest == null || executeFeatureRequest.feature == null
                || executeFeatureRequest.command == null || executeFeatureRequest.params == null) {
            return Response.ok(new ErrorInfo("ARRETE")).status(400).build();
        }

        String grp = "";
        for (String tmp : jwt.getGroups()) {
            grp = tmp;
            break;
        }

        if (grp.equals("user")) {
            String idstr = jwt.getSubject();
            UUID realId = UUID.fromString(idstr);
            Boolean ok = false;
            for (ProjectModel temp : projectService.getProjects()) {
                for (UserModel u : temp.members) {
                    if (u.id.equals(realId)) {
                        ok = true;
                        break;
                    }
                }
            }
            if (!ok) {
                return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(403).build();
            }
        }

        ProjectModel project = projectService.getProject(id);
        if (project == null) {
            return Response.ok(new ErrorInfo("Je t'ai pas trouve gros...")).status(404).build();
        }

        // TODO
        // Faire le gros du travail

        return Response.ok(new SimpleMessageResponse("Yo la team")).status(204).build();
    }

    @POST
    @Path("/{id}/remove-user")
    @RolesAllowed({ "admin", "user" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeMember(@PathParam("id") UUID id, AddMemberToProjectRequest addMemberToProjectRequest) {
        if (addMemberToProjectRequest == null || addMemberToProjectRequest.userId == null) {
            return Response.ok(new ErrorInfo("ARRETE")).status(400).build();
        }

        String grp = "";
        for (String tmp : jwt.getGroups()) {
            grp = tmp;
            break;
        }

        if (grp.equals("user")) {
            String idstr = jwt.getSubject();
            UUID realId = UUID.fromString(idstr);
            Boolean ok = false;
            for (ProjectModel temp : projectService.getProjects()) {
                if (temp.owner.id.equals(realId)) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(403).build();
            }
        }

        ProjectModel project = projectService.getProject(id);
        UserModel newMember = userService.GetUser(addMemberToProjectRequest.userId);
        if (project == null || newMember == null) {
            return Response.ok(new ErrorInfo("Je t'ai pas trouve gros...")).status(404).build();
        }

        if (project.owner.id.equals(newMember.id)) {
            return Response.ok(new ErrorInfo("On va pas kick le chef du groupe en vrai")).status(409).build();
        }

        project.members.remove(newMember); // jsp si ca marche

        return Response.ok(new SimpleMessageResponse("Yo la team")).status(204).build();
    }

}