package io.tavisco.rvstore.cars;

import lombok.extern.java.Log;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.Set;

@Log
@Path("/api/debug")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class DebugResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    @Claim("groups")
    Set<String> groups;

    @GET
    @Path("/auth")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    public String helloRolesAllowed(@Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String name = caller == null ? "anonymous" : caller.getName();
        String uid = jwt.getClaim("uid");
        String nickname = jwt.getClaim("nickname");
        boolean hasJWT = jwt != null;
        String groupsString = groups != null ? groups.toString() : "";
        return String.format("hello + %s, isSecure: %s, authScheme: %s, hasJWT: %s, uid: %s, nickname: %s groups: %s", name,
                ctx.isSecure(), ctx.getAuthenticationScheme(), hasJWT, uid, nickname, groupsString);
    }

}
