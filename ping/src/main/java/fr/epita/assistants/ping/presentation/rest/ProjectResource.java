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

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.io.File;
import java.time.*;

import fr.epita.assistants.ping.utils.Logger;

@Path("/api/projects")
public class ProjectResource {
    @Inject
    Logger Logger;
    @Inject
    ProjectService projectService;

    @Inject
    UserService userService;

    @Inject
    JsonWebToken jwt;

    @ConfigProperty(name = "PROJECT_DEFAULT_PATH", defaultValue = "")
    String projectsPath;
    // private static final String PROJECT_DEFAULT_PATH =
    // System.getenv("PROJECT_DEFAULT_PATH");

    @GET
    @Path("/")
    @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_JSON)
    public Response userProjects(@QueryParam("onlyOwned") Boolean onlyOwned) {
        if (onlyOwned == null) {
            onlyOwned = false;
        }
        Logger.logRequest(jwt.getSubject(), "/api/projects/", "GEt " + onlyOwned.toString());
        UUID id = UUID.fromString(jwt.getSubject());
        ArrayList<ProjectResponse> response = new ArrayList<ProjectResponse>();

        for (ProjectModel pm : projectService.getUserProjects(id)) {
            ArrayList<MemberResponse> mr = new ArrayList<MemberResponse>();

            for (UserModel um : pm.members) {
                mr.add(new MemberResponse(um.id, um.displayName, um.avatar));
            }
            if (onlyOwned) {
                if (id.equals(pm.owner.id)) {
                    response.add(new ProjectResponse(pm.id, pm.name, mr,
                            new MemberResponse(pm.owner.id, pm.owner.displayName, pm.owner.avatar)));
                }
            } else {
                response.add(new ProjectResponse(pm.id, pm.name, mr,
                        new MemberResponse(pm.owner.id, pm.owner.displayName, pm.owner.avatar)));
            }
        }
        Logger.logRequest(jwt.getSubject(), "/api/projects/", "GEt " + "all ok !!!");
        return Response.ok(response).status(200).build();
    }

    @POST
    @Path("/")
    @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProjects(ProjectRequest request) {
        if (request == null || request.name == null || request.name.length() == 0 || request.name.isBlank()) {
            return Response.ok(new ErrorInfo("yo dsl, Nathalie vient de m'appeler, ca va etre chaud la...")).status(400)
                    .build();
        }
        Logger.logRequest(jwt.getSubject(), "/api/projects/", "POST " + request.name);
        UUID id = UUID.fromString(jwt.getSubject());
        UserModel owner = userService.getUser(id);

        ProjectModel project = projectService.addProject(request.name, owner);

        ArrayList<MemberResponse> mr = new ArrayList<MemberResponse>();

        for (UserModel um : project.members) {
            mr.add(new MemberResponse(um.id, um.displayName, um.avatar));
        }

        Logger.logRequest(jwt.getSubject(), "/api/projects/", "POST " + "all ok !!!");
        return Response.ok(new ProjectResponse(project.id, project.name, mr,
                new MemberResponse(project.owner.id, project.owner.displayName, project.owner.avatar))).status(200)
                .build();
    }

    @GET
    @Path("/all")
    @RolesAllowed({ "admin" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response allProjects() {
        List<ProjectResponse> response = new ArrayList<ProjectResponse>();
        Logger.logRequest(jwt.getSubject(), "/api/projects/all", "");

        for (ProjectModel pm : projectService.getProjects()) {
            List<MemberResponse> mr = new ArrayList<MemberResponse>();
            for (UserModel um : pm.members) {
                mr.add(new MemberResponse(um.id, um.displayName, um.avatar));
            }
            response.add(new ProjectResponse(pm.id, pm.name, mr,
                    new MemberResponse(pm.owner.id, pm.owner.displayName, pm.owner.avatar)));
        }
        Logger.logRequest(jwt.getSubject(), "/api/projects/all", "all ok !!!");
        return Response.ok(response).status(200).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProject(@PathParam("id") UUID id, UpdateProjectRequest updateProjectRequest) {

        if (updateProjectRequest == null
                || (updateProjectRequest.name == null && updateProjectRequest.newOwnerId == null)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}", "PUT " + "error body is incorrect");
            return Response.ok(new ErrorInfo("Nan la c'est abuse en vrai"))
                    .status(400).build();
        }
        String name = updateProjectRequest.name;
        UUID newId = updateProjectRequest.newOwnerId;
        if (name == null) {
            name = "";
        }

        Logger.logRequest(jwt.getSubject(), "/api/projects/{id}",
                id.toString() + updateProjectRequest.name + " " + updateProjectRequest.newOwnerId);

        String grp = "";
        for (String tmp : jwt.getGroups()) {
            grp = tmp;
            break;
        }

        if (grp.equals("user")) {
            String idstr = jwt.getSubject();
            UUID realId = UUID.fromString(idstr);
            Boolean ok = false;
            ProjectModel p = projectService.getProject(id);
            if (p.owner.id.equals(realId)) {
                ok = true;
            }
            if (!ok) {
                Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}", "PUT " + "error not ok");
                return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(403).build();
            }
        }

        ProjectModel p = projectService.updateProject(name, id, newId);
        if (p == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}", "PUT " + "error p null");
            return Response.ok(new ErrorInfo("ARRETE Nan la c'est abuse en vrai"))
                    .status(404).build();
        }

        ArrayList<MemberResponse> mr = new ArrayList<MemberResponse>();

        for (UserModel um : p.members) {
            mr.add(new MemberResponse(um.id, um.displayName, um.avatar));
        }

        Logger.logRequest(jwt.getSubject(), "/api/projects/{id}", "PUT " + "all ok !!!");
        return Response.ok(new ProjectResponse(p.id, p.name, mr,
                new MemberResponse(p.owner.id, p.owner.displayName, p.owner.avatar))).status(200)
                .build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProject(@PathParam("id") UUID id) {
        Logger.logRequest(jwt.getSubject(), "/api/projects/{id}", "GET " + id.toString());
        String grp = "";
        for (String tmp : jwt.getGroups()) {
            grp = tmp;
            break;
        }

        if (grp.equals("user")) {
            String idstr = jwt.getSubject();
            UUID realId = UUID.fromString(idstr);
            Boolean ok = false;
            ProjectModel p = projectService.getProject(id);
            for (UserModel temp : p.members) {
                if (temp.id.equals(realId)) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}", "PUT " + "error ok false");
                return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(403).build();
            }
        }

        ProjectModel project = projectService.getProject(id);
        if (project == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}", "PUT " + "error project null");
            return Response.ok(new ErrorInfo("Y'a rien la")).status(404).build();
        }

        ArrayList<MemberResponse> mr = new ArrayList<MemberResponse>();

        for (UserModel um : project.members) {
            mr.add(new MemberResponse(um.id, um.displayName, um.avatar));
        }

        Logger.logRequest(jwt.getSubject(), "/api/projects/{id}", "GET " + "all ok !!!");
        return Response.ok(new ProjectResponse(project.id, project.name, mr,
                new MemberResponse(project.owner.id, project.owner.displayName, project.owner.avatar))).status(200)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteProject(@PathParam("id") UUID id) {
        Logger.logRequest(jwt.getSubject(), "/api/projects/{id}", "DELETE " + id.toString());

        ProjectModel project = projectService.getProject(id);
        if (project == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}", "DELETE " + "error project null");
            return Response.ok(new ErrorInfo("Y'a rien la")).status(404).build();
        }

        String grp = "";
        for (String tmp : jwt.getGroups()) {
            grp = tmp;
            break;
        }

        if (grp.equals("user")) {
            String idstr = jwt.getSubject();
            UUID realId = UUID.fromString(idstr);
            if (!realId.equals(project.owner.id)) {
                Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}", "DELETE " + "error ok false");
                return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(403).build();
            }
        }

        projectService.deleteProject(id);
        Logger.logRequest(jwt.getSubject(), "/api/projects/{id}", "DELETE " + "all ok !!!");
        return Response.ok(new SimpleMessageResponse("WOW")).status(204).build();
    }

    @POST
    @Path("/{id}/add-user")
    @RolesAllowed({ "admin", "user" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMember(@PathParam("id") UUID id, AddMemberToProjectRequest addMemberToProjectRequest) {
        if (addMemberToProjectRequest == null || addMemberToProjectRequest.userId == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}/add-user", "error body incorrect");
            return Response.ok(new ErrorInfo("ARRETE")).status(400).build();
        }
        Logger.logRequest(jwt.getSubject(), "/api/projects/{id}/add-user",
                id.toString() + addMemberToProjectRequest.userId.toString());

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
                Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}/add-user", "error ok false");
                return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(403).build();
            }
        }

        ProjectModel project = projectService.getProject(id);
        UserModel newMember = userService.getUser(addMemberToProjectRequest.userId);
        if (project == null || newMember == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}/add-user", "error p null n null");
            return Response.ok(new ErrorInfo("Je t'ai pas trouve gros...")).status(404).build();
        }

        for (UserModel user : project.members) {
            if (user.id.equals(newMember.id)) {
                Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}/add-user", "error user already present");
                return Response.ok(new ErrorInfo("T'es deja dans le projet faut suivre un peu...")).status(409).build();
            }
        }

        projectService.addUserToProject(id, newMember);
        Logger.logRequest(jwt.getSubject(), "/api/projects/{id}/add-user", "all ok !!!");
        return Response.ok(new SimpleMessageResponse("Yo la team")).status(204).build();
    }

    @POST
    @Path("/{id}/exec")
    @RolesAllowed({ "admin", "user" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response executeFeature(@PathParam("id") UUID id, ExecuteFeatureRequest executeFeatureRequest) {
        if (executeFeatureRequest == null || executeFeatureRequest.feature == null
                || executeFeatureRequest.command == null || !executeFeatureRequest.feature.equals("git")) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}/add-user", "error bodu incorrect");
            return Response.ok(new ErrorInfo("ARRETE")).status(400).build();
        }
        if (!executeFeatureRequest.command.equals("init") && !executeFeatureRequest.command.equals("commit")
                && !executeFeatureRequest.command.equals("add")) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}/add-user", "TU ME DIS N IMPORTE QUOI");
            return Response.ok(new ErrorInfo("ARRETE")).status(400).build();
        }
        if (executeFeatureRequest.command.equals("add")
                && (executeFeatureRequest.params == null || executeFeatureRequest.params.size() == 0)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}/add-user", "TU ME DIS N IMPORTE QUOI");
            return Response.ok(new ErrorInfo("ARRETE")).status(400).build();
        }
        List<String> fin = new ArrayList<String>();
        fin.add(executeFeatureRequest.feature);
        fin.add(executeFeatureRequest.command);
        String liststr = "";
        if (executeFeatureRequest.params != null) {
            for (String s : executeFeatureRequest.params) {
                liststr += s;
                liststr += " ";
                fin.add(s);
            }
        }
        Logger.logRequest(jwt.getSubject(), "/api/projects/{id}/exec",
                id.toString() + " " + executeFeatureRequest.feature + " " + executeFeatureRequest.command + " "
                        + liststr);

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
                Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}/exec", "error ok false");
                return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(403).build();
            }
        }

        ProjectModel project = projectService.getProject(id);
        if (project == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}/exec", "error project null");
            return Response.ok(new ErrorInfo("Je t'ai pas trouve gros...")).status(404).build();
        }

        // Faire le gros du travail
        ProcessBuilder pbBuilder = new ProcessBuilder().command(fin)
                .directory(new File(projectsPath + "/" + id.toString() + "/"));
        try {
            Process process = pbBuilder.start();
            int output = process.waitFor();

            if (output != 0) {
                Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}/exec",
                        "J AI PAS PU RUN TA COMMANDE NOEUILLE");
                return Response.ok(new ErrorInfo("PROUT")).status(400).build();
            }

        } catch (Exception e) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}/exec", "JE VIENS D EXPLOSER");
            return Response.ok(new ErrorInfo("MACRON")).status(400).build();
        }

        Logger.logRequest(jwt.getSubject(), "/api/projects/{id}/exec", "all ok !!!");
        return Response.ok(new SimpleMessageResponse("Yo la team")).status(204).build();
    }

    @POST
    @Path("/{id}/remove-user")
    @RolesAllowed({ "admin", "user" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeMember(@PathParam("id") UUID id, AddMemberToProjectRequest addMemberToProjectRequest) {
        if (addMemberToProjectRequest == null || addMemberToProjectRequest.userId == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}/remove-user", "error project null");
            return Response.ok(new ErrorInfo("ARRETE")).status(400).build();
        }
        Logger.logRequest(jwt.getSubject(), "/api/projects/{id}/remove-user",
                id.toString() + addMemberToProjectRequest.userId.toString());

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
                Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}/remove-user", "error ok false");
                return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(403).build();
            }
        }

        ProjectModel project = projectService.getProject(id);
        UserModel newMember = userService.getUser(addMemberToProjectRequest.userId);
        if (project == null || newMember == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}/remove-user", "error project null");
            return Response.ok(new ErrorInfo("Je t'ai pas trouve gros...")).status(404).build();
        }

        if (project.owner.id.equals(newMember.id)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{id}/remove-user", "error id equals");
            return Response.ok(new ErrorInfo("On va pas kick le chef du groupe en vrai")).status(403).build();
        }

        if (!projectService.deleteUserFromProject(id, newMember)) {
            // Pas trouve
            return Response.ok(new ErrorInfo("NAN YUKI")).status(404).build();
        }
        Logger.logRequest(jwt.getSubject(), "/api/projects/{id}/remove-user", "all ok !!!");
        return Response.ok(new SimpleMessageResponse("Yo la team")).status(204).build();
    }

}