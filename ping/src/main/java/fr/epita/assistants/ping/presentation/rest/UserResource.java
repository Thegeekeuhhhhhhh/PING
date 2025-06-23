package fr.epita.assistants.ping.presentation.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import fr.epita.assistants.ping.domain.service.ProjectService;
import fr.epita.assistants.ping.domain.service.UserService;
import fr.epita.assistants.ping.common.api.request.UserRequest;
import fr.epita.assistants.ping.common.api.request.LoginRequest;
import fr.epita.assistants.ping.common.api.response.UserResponse;
import fr.epita.assistants.ping.common.api.request.UpdateRequest;
import fr.epita.assistants.ping.data.model.ProjectModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.common.api.response.LoginResponse;
import fr.epita.assistants.ping.common.api.response.RefreshResponse;
import fr.epita.assistants.ping.common.api.response.SimpleMessageResponse;
import fr.epita.assistants.ping.utils.ErrorInfo;
import io.quarkus.security.Authenticated;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;
import io.smallrye.jwt.build.JwtSignature;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;

import java.util.*;
import java.util.concurrent.TimeUnit;
//import java.util.logging.Logger;

import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.*;
import java.time.temporal.ChronoUnit;

import fr.epita.assistants.ping.utils.Logger;

@Path("/api/user")
public class UserResource {
    @Inject
    Logger Logger;
    @Inject
    UserService userService;

    @Inject
    ProjectService projectService;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/createadmin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createFirstAdmin() {
        Logger.logRequest("0", "/api/user/createadmin", "");
        String log = "admin";
        String pwd = "admin";
        var a = userService.checkUser(log, pwd);
        if (a == null) {
            UserModel u = userService.addUser("prout", "admin", true, "admin", "admin");
            String tk = Jwt.claims()
                    .issuer("ProutMan roi des cramptes")
                    .upn("Caca man")
                    .subject(u.id.toString())
                    .groups("admin")
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plus(10000, ChronoUnit.SECONDS))
                    .sign();
            return Response.ok(new LoginResponse(tk)).status(200).build();
        }

        return Response.ok(new SimpleMessageResponse("L ADMIN EXISTE DEJA")).status(200).build();

    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin" }) // 401 + 403
    public Response user(UserRequest userRequest) {

        if (userRequest == null || userRequest.login == null || userRequest.password == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/user", "error there have no body or the body is incorrect");
            return Response.ok(new ErrorInfo("Caca et pipi sont sur un bateau")).status(401).build();
        }

        Logger.logRequest(jwt.getSubject(), "/api/user/",
                userRequest.login + " " + userRequest.password + " " + userRequest.isAdmin);
        boolean legit = false;
        String first = "";
        String last = "";
        for (char c : userRequest.login.toCharArray()) {
            if (c == '.' || c == '_') {
                if (legit) {
                    // On est deja venus
                    Logger.logErrorRequest(jwt.getSubject(), "/api/user", "error the login have 2 \".\" or \"_\"");
                    return Response.ok(new ErrorInfo("NAN GROS NAN YUKI")).status(400).build();
                }
                legit = true;
            } else {
                if (!legit) {
                    first += c;
                } else {
                    last += c;
                }
            }
        }
        if (!legit) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/user", "error the login have 0 \".\" or \"_\"");
            return Response.ok(new ErrorInfo("NAN GROS NAN YUKI")).status(400).build();
        }

        first = first.substring(0, 1).toUpperCase() + first.substring(1);
        last = last.substring(0, 1).toUpperCase() + last.substring(1);
        String completeName = first + " " + last;

        // On recupere les 3 prochains fields dans la database
        String avatar = "";

        UserModel temp = userService.addUser(avatar, completeName, userRequest.isAdmin, userRequest.login,
                userRequest.password);
        if (temp == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/user", "error user already exist");
            return Response.ok(new ErrorInfo("NAN GROS NAN YUKI")).status(409).build();
        }
        Logger.logRequest(jwt.getSubject(), "/api/user/", "all ok !!!");
        return Response.ok(new UserResponse(temp.id, userRequest.login, completeName, userRequest.isAdmin, avatar))
                .status(200)
                .build();
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin" }) // 401 + 403
    public Response listUsers() {
        Logger.logRequest(jwt.getSubject(), "/api/user/all", "");
        List<UserModel> temp = userService.listUsers();
        ArrayList<UserResponse> res = new ArrayList<UserResponse>();
        for (UserModel u : temp) {
            UserResponse x = new UserResponse(u.id, u.login, u.displayName, u.isAdmin, u.avatar);
            res.add(x);
        }
        Logger.logRequest(jwt.getSubject(), "/api/user/all", "all ok !!!");
        return Response.ok(res).status(200).build();
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest loginRequest) {

        System.out.println(jwt.toString());
        System.out.println("J ARRIVE");
        if (loginRequest == null || loginRequest.login == null || loginRequest.password == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/user/login", "error on body");
            return Response.ok(new ErrorInfo("LEO VRAIMENT TU FAIT CA")).status(400).build();
        }
        Logger.logRequest(jwt.getSubject(), "/api/user/login", loginRequest.login + " " + loginRequest.password);
        var a = userService.checkUser(loginRequest.login, loginRequest.password);
        if (a == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/user/login", "error check user null");
            return Response.ok(new ErrorInfo("KENAN C EST VRAIMENT PAS BIEN")).status(401).build();
        }

        String tk;

        if (a.isAdmin) {
            tk = Jwt.claims()
                    .issuer("ProutMan roi des cramptes")
                    .subject(a.id.toString())
                    .groups("admin")
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plus(10000, ChronoUnit.SECONDS))
                    .sign();
        } else {
            tk = Jwt.claims()
                    .issuer("ProutMan roi des cramptes")
                    .subject(a.id.toString())
                    .groups("user")
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plus(10000, ChronoUnit.SECONDS))
                    .sign();
        }

        System.out.println(tk.toString());
        Logger.logRequest(jwt.getSubject(), "/api/user/login", "all ok !!!");
        return Response.ok(new LoginResponse(tk)).build();

    }

    @GET
    @Path("/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin", "user" }) // 401 + 403
    public Response refreshToken() {
        // 403 a gerer jsp comment differencier 403 et 404
        Logger.logRequest(jwt.getSubject(), "/api/user/refresh", "");
        String grp = ""; // recup le groupe du jwt (horrible)
        for (String s : jwt.getGroups()) {
            grp = s;
            break;
        }

        String id = jwt.getSubject();
        UUID realId = UUID.fromString(id);
        List<UserModel> list = userService.listUsers();
        for (UserModel x : list) {
            if (x.id.equals(realId)) {
                String tk = Jwt.claims()
                        .issuer("ProutMan roi des cramptes")
                        .subject(x.id.toString())
                        .groups(grp)
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plus(10000, ChronoUnit.SECONDS))
                        .sign();
                Logger.logRequest(jwt.getSubject(), "/api/user/refresh", "all ok !!!");
                return Response.ok(new RefreshResponse(tk)).status(200)
                        .build();
            }
        }
        Logger.logErrorRequest(jwt.getSubject(), "/api/user/refresh", "error user not found");
        return Response.ok(new ErrorInfo("USER NOT FOUND ARRETE")).status(404).build();
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin", "user" }) // 401 + 403.5
    public Response updateUser(@PathParam("id") UUID id, UpdateRequest updateRequest) {
        Logger.logRequest(jwt.getSubject(), "/api/user/{id}",
                "PUT " + updateRequest.password + " " + updateRequest.displayName + " " + updateRequest.avatar);
        String grp = "";
        for (String tmp : jwt.getGroups()) {
            grp = tmp;
            break;
        }

        if (grp.equals("user")) {
            String idstr = jwt.getSubject();
            UUID realId = UUID.fromString(idstr);
            if (!realId.equals(id)) {
                Logger.logErrorRequest(jwt.getSubject(), "/api/user/{id}", "PUT " + "error wrong rigth");
                return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(403).build();
            }
        }

        String dpname = updateRequest.displayName;
        String avatar = updateRequest.avatar;
        String passwd = updateRequest.password;

        if (dpname == null || dpname.isBlank()) {
            dpname = "";
        }
        if (avatar == null || avatar.isBlank()) {
            avatar = "";
        }
        if (passwd == null || passwd.isBlank()) {
            passwd = "";
        }

        UserModel user = userService.updateUser(id, dpname, avatar, passwd);
        if (user == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/user/{id}", "PUT " + "error user null");
            return Response.ok(new ErrorInfo("KENAN C EST VRAIMENT PAS BIEN")).status(404).build();
        }
        Logger.logRequest(jwt.getSubject(), "/api/user/{id}", "PUT " + "all ok !!!");
        return Response.ok(new UserResponse(user.id, user.login, user.displayName, user.isAdmin, user.avatar))
                .status(200).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin", "user" }) // 401 + 403.5
    public Response GetUser(@PathParam("id") UUID id) {
        Logger.logRequest(jwt.getSubject(), "/api/user/{id}", "GET " + id.toString());
        String grp = "";
        for (String tmp : jwt.getGroups()) {
            grp = tmp;
            break;
        }

        if (grp.equals("user")) {
            String idstr = jwt.getSubject();
            UUID realId = UUID.fromString(idstr);
            if (!realId.equals(id)) {
                Logger.logErrorRequest(jwt.getSubject(), "/api/user/{id}", "GEt " + "error les droits des femmes");
                return Response.ok(new ErrorInfo("TU N'AS PAS LE DROIT ARRETE")).status(403).build();
            }
        }

        UserModel user = userService.getUser(id);
        // 403a gere
        if (user == null) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/user/{id}", "GEt " + "error user null");
            return Response.ok(new ErrorInfo("KENAN C EST VRAIMENT PAS BIEN")).status(404).build();
        }
        Logger.logRequest(jwt.getSubject(), "/api/user/{id}", "GET " + "all ok !!!");
        return Response.ok(new UserResponse(user.id, user.login, user.displayName, user.isAdmin, user.avatar)).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin" }) // 401 + 403
    public Response DeleteUser(@PathParam("id") UUID id) {
        Logger.logRequest(jwt.getSubject(), "/api/user/{id}", "DELETE " + id.toString());
        List<ProjectModel> list = projectService.getUserProjects(id);
        if (list.size() > 0) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/user/{id}", "GEt " + "error no user");
            return Response.ok(new ErrorInfo("PAS LE DROIT DE DELETE")).status(403).build();
        }

        Boolean bool = userService.deleteUser(id);
        if (bool == false) {
            Logger.logErrorRequest(jwt.getSubject(), "/api/user/{id}", "GEt " + "error bool false");
            return Response.ok(new ErrorInfo("KENAN C EST VRAIMENT PAS BIEN")).status(404).build();
        }
        Logger.logRequest(jwt.getSubject(), "/api/user/{id}", "DELETE " + "all ok !!!");
        return Response.ok().status(204).build();
    }
}