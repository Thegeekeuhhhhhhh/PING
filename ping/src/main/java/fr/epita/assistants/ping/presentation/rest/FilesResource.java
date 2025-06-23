package fr.epita.assistants.ping.presentation.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import fr.epita.assistants.ping.domain.service.FilesService;
import fr.epita.assistants.ping.domain.service.ProjectService;
import fr.epita.assistants.ping.domain.service.UserService;
import fr.epita.assistants.ping.common.api.request.CreateFileRequest;
import fr.epita.assistants.ping.common.api.request.DeleteFileRequest;
import fr.epita.assistants.ping.common.api.request.MoveFileRequest;
import fr.epita.assistants.ping.data.model.ProjectModel;
import fr.epita.assistants.ping.common.api.response.SimpleMessageResponse;
import fr.epita.assistants.ping.utils.ErrorInfo;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;

import java.util.*;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;

import fr.epita.assistants.ping.utils.Logger;

@Path("/api/projects/{projectId}/files")
public class FilesResource {
    @Inject
    Logger Logger;
    
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
        // TODO / NOAH DONE
        // On prend en parametre un chemin relatif
        // par exemple:
        // isPathTraversalAttack("/") -> C'est pas une attaque parce qu'on sort pas de
        // root

        // Il faut juste detecter si a un moment on est deja a la racine et qu'on essaye
        // de sortir

        // isPathTraversalAttack("prout/caca/../../..") -> On va sortir du projet donc
        // on renvoie true

        // Noah: Une version temporaire voir définition root selon léo

        try {
            /*TODO A modifier important*/ var root = Paths.get("/").normalize().toAbsolutePath();
            var target = root.resolve(path).normalize();
            return !target.startsWith(root);
        }catch (Exception e) {
            return true;
        }
    }

    Boolean isPathNotFound(String path) {
        return !new File(path).exists();
    }

    Boolean doFileAlreadyExists(String path) {
        return new File(path).exists();
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

        // TODO / NOAH DONE
        // Create file -> ez pz (jsp ce qu'on met comme content)
        try {
            File file = new File(projectsPath + "/" + id.toString() + "/" + createFileRequest.relativePath);
            file.getParentFile().mkdirs();
            boolean created = file.createNewFile();
            if (!created) { // peut-être pas nécéssaire
                Logger.error("File creation test already exist");
                return Response.ok(new ErrorInfo("Already exists")).status(409).build();
            }
        } catch (Exception e) {
                Logger.error("File creation test Internal Error");
                return Response.ok(new ErrorInfo("Internal Error")).status(500).build();
        }

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
        // Je sais pas non plus

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

        // TODO / NOAH DONE
        // Move file from src to dst
        File srcFile = new File(projectsPath + "/" + id.toString() + "/" + moveFileRequest.src);
        File dstFile = new File(projectsPath + "/" + id.toString() + "/" + moveFileRequest.dst);

        if (!srcFile.exists()) {
            Logger.error("Source not found");
            return Response.ok(new ErrorInfo("Cannot move file - Source not found")).status(404).build();
        }

        dstFile.getParentFile().mkdirs(); // Crée les dossiers nécessaires
        boolean success = srcFile.renameTo(dstFile);
        if (!success) {
            Logger.error("Cannot move file");
            return Response.ok(new ErrorInfo("Cannot move file")).status(500).build();
        }

        return Response.ok(new SimpleMessageResponse("yo")).status(204).build();
    }

    @POST
    @Path("/upload")
    @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response uploadFile(@PathParam("projectId") UUID id, @QueryParam("path") String path,
            InputStream inputStream) {
        byte[] content;
        try {
            content = inputStream.readAllBytes();
        } catch (Exception e) {
            Logger.error("Fais un effort brozer - uploadFile");
            return Response.ok(new ErrorInfo("Fais un effort brozer")).status(400).build();
        }

        // 400, path invalid
        if (path == null || isPathInvalid(path)) {
            Logger.error("Fais un effort brozer 2 - uploadFile");
            return Response.ok(new ErrorInfo("Fais un effort brozer 2")).status(400).build();
        }

        // TODO
        // Je sais pas si on met le chemin relatif ou absolu dans la request
        // Je sais pas non plus

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

        // TODO / DONE NOAH
        // Create file and write in it
        try {
            File file = new File(projectsPath + "/" + id.toString() + "/" + path);
            file.getParentFile().mkdirs(); // Crée les dossiers si besoin
            java.nio.file.Files.write(file.toPath(), content);
        } catch (Exception e) {
            Logger.error("Could not write file - upload file error");
            return Response.ok(new ErrorInfo("Could not write file")).status(500).build();
        }

        return Response.ok(new SimpleMessageResponse("yo")).status(201).build();
    }
}