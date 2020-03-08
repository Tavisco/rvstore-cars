package io.tavisco.rvstore.cars;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import io.quarkus.panache.common.Sort;
import lombok.extern.java.Log;

/**
 * CarResource
 */
@Log
@Path("/cars")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class CarResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    @Claim("groups")
    private Set<String> groups;

    @GET
    @Path("/auth")
    @RolesAllowed({ "Everyone" })
    @Produces(MediaType.TEXT_PLAIN)
    public String helloRolesAllowed(@Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String name = caller == null ? "anonymous" : caller.getName();
        boolean hasJWT = jwt != null;
        String groupsString = groups != null ? groups.toString() : "";
        String helloReply = String.format("hello + %s, isSecure: %s, authScheme: %s, hasJWT: %s, groups: %s\"", name,
                ctx.isSecure(), ctx.getAuthenticationScheme(), hasJWT, groupsString);
        return helloReply;
    }

    @GET
    @PermitAll
    public List<Car> getAllItems() {
        log.finest("Getting all items...");
        return Car.listAll(Sort.by("name"));
    }

    @GET
    @PermitAll
    @Path("/search/{name}")
    public List<Car> findByName(@PathParam String name) {
        return Car.findByName(name);
    }

    @GET
    @PermitAll
    @Path("{id}")
    public Car findById(@PathParam Long id) {
        Car car = Car.findById(id);
        if (car == null) {
            throw new WebApplicationException("Could not find a car with ID " + id, Status.NOT_FOUND);
        }
        return car;
    }

    @POST
    @Transactional
    @RolesAllowed({ "Everyone" })
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response newCar(@Context UriInfo uriInfo, @MultipartForm Car newCar) throws IOException {
        log.finest("Receive a new car");
        if (newCar.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", Status.NOT_ACCEPTABLE);
        }

        // TODO: Handle the byte[]

        Car.persist(newCar);
        return Response.ok(newCar).status(Status.CREATED).build();
    }
    
}