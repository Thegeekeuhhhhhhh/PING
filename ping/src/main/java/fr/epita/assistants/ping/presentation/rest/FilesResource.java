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
import fr.epita.assistants.ping.data.model.UserModel;
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

    Boolean isPathTraversalAttack(String path, UUID id) {
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
            /* TODO A modifier important */ var root = Paths.get(projectsPath + "/" + id.toString() + "/").normalize()
                    .toAbsolutePath();
            var target = root.resolve(projectsPath + "/" + id.toString() + "/" + path).normalize();
            return !target.startsWith(root);
        } catch (Exception e) {
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
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The relative path is invalid (null or empty for example)");
            return Response.ok(new ErrorInfo("The relative path is invalid (null or empty for example)")).status(400)
                    .build();
        }

        Logger.logRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                id.toString() + " src: " + path);

        // 401, -> Deja fait

        // 404 incongru
        ProjectModel p = projectService.getProject(id);
        if (p == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The project or the relative path could not be found");
            return Response.ok(new ErrorInfo("The project or the relative path could not be found")).status(404)
                    .build();
        }

        // 403, check path traversal attack
        if (isUserNotAllowed(p, jwt.getGroups(), UUID.fromString(jwt.getSubject()))
                || isPathTraversalAttack(path, id)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The user is not allowed to access the project or a path traversal attack was detected");
            return Response
                    .ok(new ErrorInfo(
                            "The user is not allowed to access the project or a path traversal attack was detected"))
                    .status(403).build();
        }

        // 404, Path not found
        if (isPathNotFound(projectsPath + "/" + id.toString() + "/" + path)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The project or the relative path could not be found");
            return Response.ok(new ErrorInfo("The project or the relative path could not be found")).status(404)
                    .build();
        }

        byte[] res = filesService.getFileContent(projectsPath + "/" + id.toString() + "/" + path);
        if (res == null) {
            // 404 i guess ?
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The project or the relative path could not be found");
            return Response.ok(new ErrorInfo("The project or the relative path could not be found")).status(404)
                    .build();
        }

        Logger.logRequest(jwt.getSubject(), "/api/projects/{projectId}/files/", "Content of the file");
        return Response.ok(res).status(200).build();
    }

    @DELETE
    @Path("/")
    @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response deleteFile(@PathParam("projectId") UUID id, DeleteFileRequest deleteFileRequest) {

        // 400, path invalid
        if (deleteFileRequest == null || isPathInvalid(deleteFileRequest.relativePath)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The relative path is invalid (null or empty for example)");
            return Response.ok(new ErrorInfo("Fais un effort brozer")).status(400).build();
        }

        Logger.logRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                id.toString() + " src: " + deleteFileRequest.relativePath);

        // 401, -> Deja fait

        // 404 incongru
        ProjectModel p = projectService.getProject(id);
        if (p == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The project or the file could not be found");
            return Response.ok(new ErrorInfo("The project or the file could not be found")).status(404).build();
        }

        // 403, check path traversal attack
        if (isUserNotAllowed(p, jwt.getGroups(), UUID.fromString(jwt.getSubject()))
                || isPathTraversalAttack(deleteFileRequest.relativePath, id)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The user is not allowed to access the project or a path traversal attack was detected");
            return Response
                    .ok(new ErrorInfo(
                            "The user is not allowed to access the project or a path traversal attack was detected"))
                    .status(403).build();
        }

        // 404, Path not found
        if (isPathNotFound(projectsPath + "/" + id.toString() + "/" + deleteFileRequest.relativePath)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The project or the file could not be found");
            return Response.ok(new ErrorInfo("The project or the file could not be found")).status(404).build();
        }

        filesService.launchDelete(deleteFileRequest.relativePath, id);

        Logger.logRequest(jwt.getSubject(), "/api/projects/{projectId}/files/", "The file was deleted");
        return Response.ok(new SimpleMessageResponse("yo")).status(204).build();
    }

    @POST
    @Path("/")
    @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response createFile(@PathParam("projectId") UUID id, CreateFileRequest createFileRequest) {

        // 400, path invalid
        if (createFileRequest == null || isPathInvalid(createFileRequest.relativePath)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The relative path is invalid (null or empty for example)");
            return Response.ok(new ErrorInfo("The relative path is invalid (null or empty for example)")).status(400)
                    .build();
        }

        Logger.logRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                id.toString() + " src: " + createFileRequest.relativePath);

        // 401, -> Deja fait

        // 404 incongru
        ProjectModel p = projectService.getProject(id);
        if (p == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The project could not be found");
            return Response.ok(new ErrorInfo("The project could not be found")).status(404).build();
        }

        // 403, check path traversal attack
        String grp = "";
        for (String tmp : jwt.getGroups()) {
            grp = tmp;
            break;
        }

        UUID realId = UUID.fromString(jwt.getSubject());

        if (grp.equals("user")) {
            Boolean ok = false;
            for (UserModel m : p.members) {
                if (m.id.equals(realId)) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/", "AAAAAAAAAAAAAAA");
                return Response.ok(new ErrorInfo("AAAAAAAAAAAAAAAAAAA")).status(403).build();
            }
        }

        if (isPathTraversalAttack(createFileRequest.relativePath, id)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The user is not allowed to access the project or a path traversal attack was detected");
            return Response
                    .ok(new ErrorInfo(
                            "The user is not allowed to access the project or a path traversal attack was detected"))
                    .status(403).build();
        }

        // 409, file exists
        if (doFileAlreadyExists(projectsPath + "/" + id.toString() + "/" + createFileRequest.relativePath)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/", "The file already exists");
            return Response.ok(new ErrorInfo("The file already exists")).status(409).build();
        }

        // TODO / NOAH DONE
        // Create file -> ez pz (jsp ce qu'on met comme content)
        try {
            File file = new File(projectsPath + "/" + id.toString() + "/" + createFileRequest.relativePath);
            file.getParentFile().mkdirs();
            boolean created = file.createNewFile();
            if (!created) { // peut-être pas nécéssaire
                Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/", "The file already exists");
                return Response.ok(new ErrorInfo("The file already exists")).status(409).build();
            }
        } catch (Exception e) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/", "Server problem youstone");
            return Response.ok(new ErrorInfo("Internal Error")).status(400).build();
        }

        Logger.logRequest(jwt.getSubject(), "/api/projects/{projectId}/files/", "The file was created");
        return Response.ok(new SimpleMessageResponse("The file was created")).status(201).build();
    }

    @PUT
    @Path("/move")
    @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response moveFile(@PathParam("projectId") UUID id, MoveFileRequest moveFileRequest) {
        // 400, path invalid
        if (moveFileRequest == null || isPathInvalid(moveFileRequest.src) || isPathInvalid(moveFileRequest.dst)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The relative path is invalid (null or empty for example)");
            return Response.ok(new ErrorInfo("Fais un effort brozer")).status(400).build();
        }

        Logger.logRequest(jwt.getSubject(), "/api/projects/{projectId}/files/move/",
                id.toString() + " src: " + moveFileRequest.src + " dst: " + moveFileRequest.dst);

        // TODO
        // Je sais pas si on met le chemin relatif ou absolu dans la request
        // Je sais pas non plus

        // 401, -> Deja fait

        // 404 incongru
        ProjectModel p = projectService.getProject(id);
        if (p == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The project could not be found");
            return Response.ok(new ErrorInfo("The project could not be found")).status(404).build();
        }

        // 403, check path traversal attack
        if (isUserNotAllowed(p, jwt.getGroups(), UUID.fromString(jwt.getSubject()))
                || isPathTraversalAttack(moveFileRequest.src, id) || isPathTraversalAttack(moveFileRequest.dst, id)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The user is not allowed to access the project or a path traversal attack was detected");
            return Response.ok(new ErrorInfo("IMPOSTOR OR HACKER")).status(403).build();
        }

        // 409, file exists
        if (doFileAlreadyExists(projectsPath + "/" + id.toString() + "/" + moveFileRequest.dst)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/", "The file already exists");
            return Response.ok(new ErrorInfo("The file already exists")).status(409).build();
        }

        // TODO / NOAH DONE
        // Move file from src to dst
        File srcFile = new File(projectsPath + "/" + id.toString() + "/" + moveFileRequest.src);
        File dstFile = new File(projectsPath + "/" + id.toString() + "/" + moveFileRequest.dst);

        if (!srcFile.exists()) {
            Logger.error("Source not found");
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The project could not be found");
            return Response.ok(new ErrorInfo("The project could not be found")).status(404).build();
        }

        dstFile.getParentFile().mkdirs(); // Crée les dossiers nécessaires
        boolean success = srcFile.renameTo(dstFile);
        if (!success) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The relative path is invalid (null or empty for example)");
            return Response.ok(new ErrorInfo("The relative path is invalid (null or empty for example)")).status(400)
                    .build();
        }

        Logger.logRequest(jwt.getSubject(), "/api/projects/{projectId}/files/", "The file was created");
        return Response.ok(new SimpleMessageResponse("The file was created")).status(204).build();
    }

    @POST
    @Path("/upload")
    @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response uploadFile(@PathParam("projectId") UUID id, @QueryParam("path") String path,
            InputStream inputStream) {

        Logger.logRequest(jwt.getSubject(), "/api/projects/{projectId}/files/upload/",
                id.toString() + " " + path);

        byte[] content;
        try {
            content = inputStream.readAllBytes();
        } catch (Exception e) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The relative path is invalid (null or empty for example)");
            return Response.ok(new ErrorInfo("Fais un effort brozer")).status(400).build();
        }

        // 400, path invalid
        if (path == null || isPathInvalid(path)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The relative path is invalid (null or empty for example)");
            return Response.ok(new ErrorInfo("Fais un effort brozer 2")).status(400).build();
        }

        // TODO
        // Je sais pas si on met le chemin relatif ou absolu dans la request
        // Je sais pas non plus

        // 401, -> Deja fait

        // 404 incongru
        ProjectModel p = projectService.getProject(id);
        if (p == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The project could not be found");
            return Response.ok(new ErrorInfo("The project could not be found")).status(404).build();
        }

        // 403, check path traversal attack
        if (isUserNotAllowed(p, jwt.getGroups(), UUID.fromString(jwt.getSubject()))
                || isPathTraversalAttack(path, id)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The user is not allowed to access the project or a path traversal attack was detected");
            return Response
                    .ok(new ErrorInfo(
                            "The user is not allowed to access the project or a path traversal attack was detected"))
                    .status(403).build();
        }

        // TODO / DONE NOAH
        // Create file and write in it
        try {
            File file = new File(projectsPath + "/" + id.toString() + "/" + path);
            file.getParentFile().mkdirs(); // Crée les dossiers si besoin
            java.nio.file.Files.write(file.toPath(), content);
        } catch (Exception e) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/files/",
                    "The relative path is invalid (null or empty for example)");
            return Response.ok(new ErrorInfo("Could not write file")).status(400).build();
        }
        Logger.logRequest(jwt.getSubject(), "/api/projects/{projectId}/files/", "The file was created");
        return Response.ok(new SimpleMessageResponse("The file was created")).status(201).build();
    }
}