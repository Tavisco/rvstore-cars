package io.tavisco.rvstore.cars;

import java.net.URI;
import java.util.List;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import io.quarkus.panache.common.Sort;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

/**
 * CarResource
 */
@Log
@Path("/cars")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CarResource {

    @GET
    public List<Car> getAllItems() {
        log.finest("Getting all items...");
        return Car.listAll(Sort.by("name"));
    }

    @GET
    @Path("/search/{name}")
    public List<Car> findByName(@PathParam String name) {
        return Car.findByName(name);
    }

    @GET
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
    public Response newCar(@Context UriInfo uriInfo, Car newCar) {
        if (newCar.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", Status.NOT_ACCEPTABLE);
        }

        Car.persist(newCar);
        return Response.ok(newCar).status(Status.CREATED).build();
    }
    
}