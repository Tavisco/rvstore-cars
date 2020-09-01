package io.tavisco.rvstore.cars;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import io.tavisco.rvstore.cars.models.Car;
import lombok.extern.java.Log;

/**
 * CarResource
 */
@Log
@Path("/api/cars")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class CarResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    @Claim("groups")
    private Set<String> groups;

    @Inject
    CarService service;

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
    public Response findAllCars() {
        List<Car> cars = service.findAllCars();
        log.finest("Total numbers of cars: " + cars.size());
        return Response.ok(cars).build();
    }

    @GET
    @PermitAll
    @Path("/search/{name}")
    public Response findByName(@PathParam String name) {
        List<Car> cars = service.findByName(name);
        log.finest("There are " + cars.size() + " with name '" + name + "'");
        return Response.ok(cars).build();
    }

    @GET
    @PermitAll
    @Path("{id}")
    public Response getCar(@PathParam Long id) {
        Car car = service.findById(id);
        if (car == null) {
            log.finest("No car found with ID: " + id);
            return Response.noContent().build();
        }

        log.finest("Found car " + id);
        return Response.ok(car).build();
    }

    @POST
    @RolesAllowed({ "Everyone" })
    public Response newCar(@Context UriInfo uriInfo, @Valid Car newCar) throws IOException {
        log.finest("Receive a new car");
        if (newCar.id != null) {
            return Response.status(Status.BAD_REQUEST).entity("The car ID must be null").build();
        }

        service.persistCar(newCar);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(newCar.id));
        return Response.created(builder.build()).build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/hello")
    public String hello() {
        return "hello";
    }
    
}