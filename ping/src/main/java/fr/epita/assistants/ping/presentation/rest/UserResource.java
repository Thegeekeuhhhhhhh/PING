package fr.epita.assistants.ping.presentation.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import fr.epita.assistants.ping.domain.service.UserService;
import fr.epita.assistants.ping.common.api.request.UserRequest;
import fr.epita.assistants.ping.common.api.request.LoginRequest;
import fr.epita.assistants.ping.common.api.response.UserResponse;
import fr.epita.assistants.ping.common.api.request.UpdateRequest;
import fr.epita.assistants.ping.common.api.response.JWTResponse;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.common.api.response.LoginResponse;
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
public class UserResource {

    @Inject
    UserService userService;

    @POST
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    // @RolesAllowed("ROLE_ADMIN")
    @Authenticated
    public Response user(UserRequest userRequest) {
        System.out.println(userRequest.login);
        if (userRequest == null) {
            return Response.ok(new ErrorInfo("Caca et pipi sont sur un bateau")).status(401).build();
        }
        // Je sais pas a quel moment renvoyer 401 ?
        if (!userRequest.isAdmin) {
            return Response.ok(new ErrorInfo("Caca et pipi sont sur un bateau")).status(403).build();
        }
        boolean legit = false;
        String first = "";
        String last = "";
        for (char c : userRequest.login.toCharArray()) {
            if (c == '.' || c == '_') {
                if (legit) {
                    // On est deja venus
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
            return Response.ok(new ErrorInfo("NAN GROS NAN YUKI")).status(400).build();
        }

        first = first.substring(0, 1).toUpperCase() + first.substring(1);
        last = last.substring(0, 1).toUpperCase() + last.substring(1);
        String completeName = first + " " + last;

        // On recupere les 3 prochains fields dans la database
        String avatar = "";

        UserModel temp = userService.add_User(avatar, completeName, userRequest.isAdmin, userRequest.login,
                userRequest.password);
        if (temp == null) {
            return Response.ok(new ErrorInfo("NAN GROS NAN YUKI")).status(409).build();
        }

        return Response.ok(new UserResponse(temp.id, userRequest.login, completeName, userRequest.isAdmin, avatar))
                .status(200)
                .build();
    }

    @GET
    @Path("/user/prout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAdmin() {
        userService.add_User("", "admin", true, "admin", "admin");
        return Response.ok().status(200).build();
    }

    @POST
    @Path("/user/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest loginRequest) {

        if (loginRequest.login == null || loginRequest.password == null) {
            return Response.ok(new ErrorInfo("LEO VRAIMENT TU FAIT CA")).status(400).build();
        }
        var a = userService.checkUser(loginRequest.login, loginRequest.password);
        if (a == null) {
            return Response.ok(new ErrorInfo("KENAN C EST VRAIMENT PAS BIEN")).status(401).build();
        }

        JsonObject jwtData = new JsonObject();
        jwtData.put("sub", a.id);
        if (a.isAdmin) {
            jwtData.put("groups", "admin");
        } else {
            jwtData.put("groups", "user");
        }

        jwtData.put("iat", Date.from(Instant.now()));
        jwtData.put("exp", Date.from(Instant.ofEpochSecond(10000)));

        String tk = Jwt.encrypt(jwtData.toString());

        System.out.println(tk);

        return Response.ok(new LoginResponse(tk)).build();

    }

    @PUT
    @Path("/user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public Response updateUser(UpdateRequest UpdateRequest, Long id) {
        UserModel user = userService.updateUser(id, UpdateRequest.displayName, UpdateRequest.avatar,
                UpdateRequest.password);
        // 403 a gere
        if (user == null) {
            return Response.ok(new ErrorInfo("KENAN C EST VRAIMENT PAS BIEN")).status(404).build();
        }
        return Response.ok(new UserResponse(user.id, user.login, user.displayName, user.isAdmin, user.avatar)).build();
    }

    @GET
    @Path("/user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated // 401
    public Response GetUser(Long id) {
        UserModel user = userService.GetUser(id);
        // 403a gere
        if (user == null) {
            return Response.ok(new ErrorInfo("KENAN C EST VRAIMENT PAS BIEN")).status(404).build();
        }
        return Response.ok(new UserResponse(user.id, user.login, user.displayName, user.isAdmin, user.avatar)).build();
    }

    @DELETE
    @Path("/user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated // 401
    public Response DeleteUser(Long id) {
        Boolean bool = userService.DeleteUser(id);
        // 403 a gere
        if (bool == false) {
            return Response.ok(new ErrorInfo("KENAN C EST VRAIMENT PAS BIEN")).status(404).build();
        }
        return Response.ok().build();
    }

    @GET
    @Path("user/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated // 401
    public Response listUsers() {
        // 403 a gere
        return Response.ok(userService.listUsers().stream().map(user -> {
            var obj = new JsonObject();
            obj.put("id", user.id);
            obj.put("login", user.login);
            obj.put("displayName", user.displayName);
            obj.put("isAdmin", user.isAdmin);
            obj.put("avatar", user.avatar);
            return obj;
        })).build();
    }
}