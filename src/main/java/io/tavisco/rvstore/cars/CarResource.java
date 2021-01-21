package io.tavisco.rvstore.cars;

import io.tavisco.rvstore.cars.dto.CarDto;
import io.tavisco.rvstore.cars.dto.FormData;
import io.tavisco.rvstore.cars.exceptions.CarNotFoundException;
import io.tavisco.rvstore.cars.models.Car;
import io.tavisco.rvstore.cars.s3.CommonResource;
import io.tavisco.rvstore.cars.s3.FileObject;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * CarResource
 */
@Slf4j
@Path("/api/cars")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class CarResource {

    // https://quarkus.io/guides/amazon-s3

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
        log.debug("There are " + cars.size() + " with name '" + name + "'");
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
        log.info("Receive a new car");

        if (CollectionUtils.isEmpty(carDto.getAuthors())) {
            log.info("Received a car without any authors!");
            return Response.status(Status.BAD_REQUEST).entity("The car should have at least one author").build();
        }

        Car savedCar = service.persistCar(new Car(carDto, jwt));
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(savedCar.id));
        return Response.created(builder.build()).build();
    }

    @GET
    @RolesAllowed({ "Everyone" })
    @Path("/my")
    public Response getMyCars() {
        log.info("Searching cars from specific user.");

        List<Car> cars = service.findByUser(jwt);
        if (CollectionUtils.isEmpty(cars)) {
            log.info("No cars found for user");
            return Response.noContent().build();
        }

        log.info("Found " + cars.size() + " cars.");
        return Response.ok(cars).build();
    }


    @POST
    @RolesAllowed({ "Everyone" })
    @Path("/{id}/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@MultipartForm FormData formData, @PathParam("id") Long id) {
        log.debug("Uploading file for car ID " + id);
        if (formData.fileName == null || formData.fileName.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        if (formData.mimeType == null || formData.mimeType.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        if (service.findById(id) == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        try {
            Car car = service.persistUploadInfo(formData, id, jwt);
            if (car != null) {
                return Response.ok().status(Status.CREATED).build();
            }
        } catch (Exception e) {
            log.info("Error while upload car.", e);
        }
        return Response.serverError().build();
    }

    @GET
    @Path("download/{objectKey}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("objectKey") String objectKey) {
        return service.downloadCarFile(objectKey);
    }

    @GET
    @Path("download/list")
    public List<FileObject> listFiles() {
        return service.listFiles();
    }

}