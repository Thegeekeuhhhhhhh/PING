package fr.epita.assistants.ping.presentation.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import fr.epita.assistants.ping.domain.service.FilesService;
import fr.epita.assistants.ping.domain.service.ProjectService;
import fr.epita.assistants.ping.domain.service.UserService;
import fr.epita.assistants.ping.data.model.ProjectModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.common.api.response.GetFileResponse;
import fr.epita.assistants.ping.utils.ErrorInfo;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

import fr.epita.assistants.ping.common.api.request.FolderDeleteRequest;
import fr.epita.assistants.ping.common.api.request.FolderMoveRequest;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.epita.assistants.ping.utils.Logger;

@Path("/api/projects/{projectId}/folders")
public class FolderResource {
    @Inject
    Logger Logger;
    @Inject
    ProjectService projectService;

    @Inject
    UserService userService;

    @Inject
    FilesService filesService;

    @Inject
    JsonWebToken jwt;

    @ConfigProperty(name = "PROJECT_DEFAULT_PATH", defaultValue = "")
    String projectsPath;

    @GET
    @Path("/")
    @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_JSON)
    public Response userFolders(@PathParam("projectId") UUID id, @QueryParam("path") String path) {
        if (path == null || path.length() == 0) {
            path = "/";
        }
        Logger.logRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", id.toString() + path);

        ProjectModel p = projectService.getProject(id);
        if (p == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "error p null");
            return Response.ok(new ErrorInfo("BAKA")).status(404).build();
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
            for (UserModel temp : p.members) {
                if (temp.id.equals(realId)) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "error ok false");
                return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(403).build();
            }
        }
        if (isPathTraversalAttack(path, id)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "GET error attack");
            return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE, de vouloir faire caca")).status(403).build();
        }
        List<GetFileResponse> res = projectService.ls(id, path);
        Logger.logRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "all ok !!!");
        return Response.ok(res).status(200).build();
    }

    @DELETE
    @Path("/")
    @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteFolders(@PathParam("projectId") UUID id, FolderDeleteRequest path) {
        if (path == null || path.relativePath == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "error path null or empty");
            return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(400).build();
        }
        String chemin = path.relativePath;
        if (chemin.isBlank()) {
            chemin = "/";
        }
        Logger.logRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", id.toString() + path.relativePath);

        ProjectModel p = projectService.getProject(id);
        if (p == null || !(new File(projectsPath + "/" + id.toString() + "/" + chemin).exists())) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "DELETET error p null");
            return Response.ok(new ErrorInfo("BAKA")).status(404).build();
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
            for (UserModel temp : p.members) {
                if (temp.id.equals(realId)) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "error ok false");
                return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(403).build();
            }
        }
        if (isPathTraversalAttack(chemin, id)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "DELETE error attack");
            return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE, de vouloir faire caca")).status(403).build();
        }

        System.out.println("miam");
        filesService.launchDelete(chemin, id);

        // if (projectService.delete(id.toString() + "/" + path.relativePath) == false)
        // {
        // Logger.logErrorRequest(jwt.getSubject(),
        // "/api/projects/{projectId}/folders/", "error folder dont exist");
        // return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT
        // ARRETE")).status(400).build();
        // }
        Logger.logRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "all ok !!!");
        return Response.ok().status(204).build();
    }

    @POST
    @Path("/")
    @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_JSON)
    public Response postFolders(@PathParam("projectId") UUID id, FolderDeleteRequest path) {
        if (path == null || path.relativePath == null || path.relativePath.length() == 0) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "error path null or empty");
            return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(400).build();
        }
        Logger.logRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", id.toString() + path.relativePath);

        ProjectModel p = projectService.getProject(id);
        if (p == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "error p null");
            return Response.ok(new ErrorInfo("BAKA")).status(404).build();
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
            for (UserModel temp : p.members) {
                if (temp.id.equals(realId)) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "error ok false");
                return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(403).build();
            }
        }
        if (isPathTraversalAttack(path.relativePath, id)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "error attack");
            return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE, de vouloir faire caca")).status(403).build();
        }
        if (projectService.createFolder(id.toString() + "/" + path.relativePath) == false) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "error folder dont exist");
            return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(409).build();
        }
        Logger.logRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "all ok !!!");
        return Response.ok().status(201).build();
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

    @PUT
    @Path("/move")
    @RolesAllowed({ "admin", "user" }) // 401 + 403
    @Produces(MediaType.APPLICATION_JSON)
    public Response putFolders(@PathParam("projectId") UUID id, FolderMoveRequest path) {
        if (path == null || path.src == null || path.src.length() == 0 || path.dst == null || path.dst.length() == 0) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "error path null or empty");
            return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(400).build();
        }
        Logger.logRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/",
                "src: " + id.toString() + path.src + " dst: " + id.toString() + path.dst);

        ProjectModel p = projectService.getProject(id);
        if (p == null || !(new File(projectsPath + "/" + id.toString() + "/" + path.src).exists())) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "error p null");
            return Response.ok(new ErrorInfo("BAKA")).status(404).build();
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
            for (UserModel temp : p.members) {
                if (temp.id.equals(realId)) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "error ok false");
                return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(403).build();
            }
        }

        if (isPathTraversalAttack(path.src, id) || isPathTraversalAttack(path.dst, id)) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "error attack");
            return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE, de vouloir faire caca")).status(403).build();
        }

        if (new File(projectsPath + "/" + id.toString() + "/" + path.dst).exists()) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "TU M EMMENES OU GROS");
            return Response.ok(new ErrorInfo("J EXISTE DEJA")).status(409).build();
        }

        if (projectService.moveFolder(projectsPath + "/" + id.toString() + "/" + path.src,
                projectsPath + "/" + id.toString() + "/" + path.dst) == false) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "error folder dont exist");
            return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(409).build();
        }
        Logger.logRequest(jwt.getSubject(), "/api/projects/{projectId}/folders/", "all ok !!!");
        return Response.ok().status(204).build();
    }
}