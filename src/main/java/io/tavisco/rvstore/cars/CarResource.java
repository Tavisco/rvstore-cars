package io.tavisco.rvstore.cars;

import io.tavisco.rvstore.cars.dto.CarDto;
import io.tavisco.rvstore.cars.models.Car;
import lombok.extern.java.Log;
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
import java.util.List;

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
    CarService service;

    @GET
    @PermitAll
    public Response findAllCars() {
        Iterable<Car> cars = service.findAllCars();
        return Response.ok(cars).build();
    }

    @GET
    @PermitAll
    @Path("/search/{name}")
    public Response findByName(@PathParam("name") String name) {
        List<Car> cars = service.findByName(name);
        log.finest("There are " + cars.size() + " with name '" + name + "'");
        return Response.ok(cars).build();
    }

    @GET
    @PermitAll
    @Path("/{id}")
    public Response getCar(@PathParam("id") Long id) {
        log.info("Searching for car with ID: " + id);

        Car car = service.findById(id);
        if (car == null) {
            log.info("No car found with ID: " + id);
            return Response.noContent().build();
        }

        log.info("Found car " + id);
        return Response.ok(car).build();
    }


    @RolesAllowed({ "Everyone" })
    @POST
    public Response newCar(@Context UriInfo uriInfo, @Valid CarDto carDto) {
        log.finest("Receive a new car");

        if (CollectionUtils.isEmpty(carDto.getAuthors())) {
            return Response.status(Status.BAD_REQUEST).entity("The car should have at least one author").build();
        }

        Car savedCar = service.persistCar(new Car(carDto, jwt));
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