package fr.epita.assistants.ping.presentation.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import fr.epita.assistants.ping.domain.service.FilesService;
import fr.epita.assistants.ping.domain.service.ProjectService;
import fr.epita.assistants.ping.domain.service.UserService;
import fr.epita.assistants.ping.common.api.request.UserRequest;
import fr.epita.assistants.ping.common.api.request.AddMemberToProjectRequest;
import fr.epita.assistants.ping.common.api.request.CreateFileRequest;
import fr.epita.assistants.ping.common.api.request.DeleteFileRequest;
import fr.epita.assistants.ping.common.api.request.ExecuteFeatureRequest;
import fr.epita.assistants.ping.common.api.request.LoginRequest;
import fr.epita.assistants.ping.common.api.request.MoveFileRequest;
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
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.*;
import fr.epita.assistants.ping.utils.Logger;

@Path("/api/projects/{projectId}/files")
public class FilesResource {

    @ConfigProperty(name = "PROJECT_DEFAULT_PATH")
    String projectsPath;

    @Inject
    ProjectService projectService;

    @Inject
    UserService userService;

    @Inject
    FilesService filesService;

    @Inject
    JsonWebToken jwt;

    Boolean isPathInvalid(String path) {
        if (path == null || path.length() == 0) {
            return true;
        }

        return false;
    }

    Boolean isUserNotAllowed(ProjectModel p, Set<String> groups, UUID id) {
        String grp = "";
        for (String tmp : groups) {
            grp = tmp;
            break;
        }

        if (grp.equals("user")) {
            Boolean ok = false;
            for (ProjectModel temp : projectService.getProjects()) {
                if (temp.owner.id.equals(id)) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                return true;
            }
        }

        return false;
    }

    Boolean isPathTraversalAttack(String path) {
        // TODO
        // On prend en parametre un chemin relatif
        // par exemple:
        // isPathTraversalAttack("/") -> C'est pas une attaque parce qu'on sort pas de
        // root

        // Il faut juste detecter si a un moment on est deja a la racine et qu'on essaye
        // de sortir

        // isPathTraversalAttack("prout/caca/../../..") -> On va sortir du projet donc
        // on renvoie true
        return false;
    }

    Boolean isPathNotFound(String path) {
        // TODO
        return false;
    }

    Boolean doFileAlreadyExists(String path) {
        // TODO
        return false;
    }

    @GET
    @Path("/")
    @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFile(@PathParam("projectId") UUID id, @QueryParam("path") String path) {

        // 400, path invalid
        if (isPathInvalid(path)) {
            return Response.ok(new ErrorInfo("Fais un effort brozer")).status(400).build();
        }

        // 401, -> Deja fait

        // 404 incongru
        ProjectModel p = projectService.getProject(id);
        if (p == null) {
            return Response.ok(new ErrorInfo("Vilain")).status(404).build();
        }

        // 403, check path traversal attack
        if (isUserNotAllowed(p, jwt.getGroups(), UUID.fromString(jwt.getSubject()))
                || isPathTraversalAttack(path)) {
            return Response.ok(new ErrorInfo("IMPOSTOR OR HACKER")).status(403).build();
        }

        // 404, Path not found
        if (isPathNotFound(projectsPath + "/" + id.toString() + "/" + path)) {
            return Response.ok(new ErrorInfo("pfffffff...")).status(404).build();
        }

        byte[] res = filesService.getFileContent(projectsPath + "/" + id.toString() + "/" + path);
        if (res == null) {
            // 404 i guess ?
            return Response.ok(new ErrorInfo("pfffffff...")).status(404).build();
        }

        Logger.logRequest(jwt.getSubject(), "/api/projects/{projectId}/files/", id.toString());
        return Response.ok(res).status(200).build();
    }

    @DELETE
    @Path("/")
    @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response deleteFile(@PathParam("projectId") UUID id, DeleteFileRequest deleteFileRequest) {

        // 400, path invalid
        if (deleteFileRequest == null || isPathInvalid(deleteFileRequest.relativePath)) {
            return Response.ok(new ErrorInfo("Fais un effort brozer")).status(400).build();
        }

        // 401, -> Deja fait

        // 404 incongru
        ProjectModel p = projectService.getProject(id);
        if (p == null) {
            return Response.ok(new ErrorInfo("Vilain")).status(404).build();
        }

        // 403, check path traversal attack
        if (isUserNotAllowed(p, jwt.getGroups(), UUID.fromString(jwt.getSubject()))
                || isPathTraversalAttack(deleteFileRequest.relativePath)) {
            return Response.ok(new ErrorInfo("IMPOSTOR OR HACKER")).status(403).build();
        }

        // 404, Path not found
        if (isPathNotFound(projectsPath + "/" + id.toString() + "/" + deleteFileRequest.relativePath)) {
            return Response.ok(new ErrorInfo("pfffffff...")).status(404).build();
        }

        filesService.launchDelete(deleteFileRequest.relativePath, id);

        return Response.ok(new SimpleMessageResponse("yo")).status(204).build();
    }

    @POST
    @Path("/")
    @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response createFile(@PathParam("projectId") UUID id, CreateFileRequest createFileRequest) {

        // 400, path invalid
        if (createFileRequest == null || isPathInvalid(createFileRequest.relativePath)) {
            return Response.ok(new ErrorInfo("Fais un effort brozer")).status(400).build();
        }

        // 401, -> Deja fait

        // 404 incongru
        ProjectModel p = projectService.getProject(id);
        if (p == null) {
            return Response.ok(new ErrorInfo("Vilain")).status(404).build();
        }

        // 403, check path traversal attack
        if (isUserNotAllowed(p, jwt.getGroups(), UUID.fromString(jwt.getSubject()))
                || isPathTraversalAttack(createFileRequest.relativePath)) {
            return Response.ok(new ErrorInfo("IMPOSTOR OR HACKER")).status(403).build();
        }

        // 409, file exists
        if (doFileAlreadyExists(projectsPath + "/" + id.toString() + "/" + createFileRequest.relativePath)) {
            return Response.ok(new ErrorInfo("pfffffff...")).status(409).build();
        }

        // TODO
        // Create file -> ez pz (jsp ce qu'on met comme content)

        return Response.ok(new SimpleMessageResponse("yo")).status(201).build();
    }

    @PUT
    @Path("/move")
    @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response moveFile(@PathParam("projectId") UUID id, MoveFileRequest moveFileRequest) {

        // 400, path invalid
        if (moveFileRequest == null || isPathInvalid(moveFileRequest.src) || isPathInvalid(moveFileRequest.dst)) {
            return Response.ok(new ErrorInfo("Fais un effort brozer")).status(400).build();
        }

        // TODO
        // Je sais pas si on met le chemin relatif ou absolu dans la request

        // 401, -> Deja fait

        // 404 incongru
        ProjectModel p = projectService.getProject(id);
        if (p == null) {
            return Response.ok(new ErrorInfo("Vilain")).status(404).build();
        }

        // 403, check path traversal attack
        if (isUserNotAllowed(p, jwt.getGroups(), UUID.fromString(jwt.getSubject()))
                || isPathTraversalAttack(moveFileRequest.src) || isPathTraversalAttack(moveFileRequest.dst)) {
            return Response.ok(new ErrorInfo("IMPOSTOR OR HACKER")).status(403).build();
        }

        // 409, file exists
        if (doFileAlreadyExists(moveFileRequest.dst)) {
            return Response.ok(new ErrorInfo("pfffffff...")).status(409).build();
        }

        // TODO
        // Move file from src to dst

        return Response.ok(new SimpleMessageResponse("yo")).status(204).build();
    }

    @POST
    @Path("/upload")
    @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response uploadFile(@PathParam("projectId") UUID id, @QueryParam("path") String path,
            InputStream inputStream) {
        try {
            byte[] content = inputStream.readAllBytes();
        } catch (Exception e) {
            return Response.ok(new ErrorInfo("Fais un effort brozer")).status(400).build();
        }

        // 400, path invalid
        if (path == null || isPathInvalid(path)) {
            return Response.ok(new ErrorInfo("Fais un effort brozer")).status(400).build();
        }

        // TODO
        // Je sais pas si on met le chemin relatif ou absolu dans la request

        // 401, -> Deja fait

        // 404 incongru
        ProjectModel p = projectService.getProject(id);
        if (p == null) {
            return Response.ok(new ErrorInfo("Vilain")).status(404).build();
        }

        // 403, check path traversal attack
        if (isUserNotAllowed(p, jwt.getGroups(), UUID.fromString(jwt.getSubject()))
                || isPathTraversalAttack(path)) {
            return Response.ok(new ErrorInfo("IMPOSTOR OR HACKER")).status(403).build();
        }

        // TODO
        // Create file and write in it

        return Response.ok(new SimpleMessageResponse("yo")).status(201).build();
    }
}