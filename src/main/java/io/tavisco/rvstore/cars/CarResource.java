package io.tavisco.rvstore.cars;

import io.tavisco.rvstore.cars.dto.CarDto;
import io.tavisco.rvstore.cars.models.Car;
import lombok.extern.java.Log;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.springframework.util.CollectionUtils;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import java.security.Principal;
import java.util.List;
import java.util.Set;

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
    Set<String> groups;

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
        return String.format("hello + %s, isSecure: %s, authScheme: %s, hasJWT: %s, groups: %s\"", name,
                ctx.isSecure(), ctx.getAuthenticationScheme(), hasJWT, groupsString);
    }

    @GET
    @PermitAll
    public Response findAllCars() {
        Iterable<Car> cars = service.findAllCars();
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
    @Path("/{id}")
    public Response getCar(@PathParam Long id) {
        log.fine("Searching for car with ID: " + id);

        if (id == null) {
            return Response.status(Status.BAD_REQUEST).entity("You should especify an ID to look up").build();
        }

        Car car = service.findById(id);
        if (car == null) {
            log.finest("No car found with ID: " + id);
            return Response.noContent().build();
        }

        log.finest("Found car " + id);
        return Response.ok(car).build();
    }


    @RolesAllowed({ "Everyone" })
    @POST
    public Response newCar(@Context UriInfo uriInfo, @Valid CarDto carDto) {
        log.finest("Receive a new car");

        if (CollectionUtils.isEmpty(carDto.getAuthors())) {
            return Response.status(Status.BAD_REQUEST).entity("The car should have at least one author").build();
        }

        Car savedCar = service.persistCar(new Car(carDto));
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(savedCar.id));
        return Response.created(builder.build()).build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/hello")
    public String hello() {
        return "hello";
    }
    
}