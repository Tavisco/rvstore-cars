package io.tavisco.rvstore.cars;

import io.tavisco.rvstore.cars.dto.FormData;
import io.tavisco.rvstore.cars.enums.CarStep;
import io.tavisco.rvstore.cars.enums.JwtCustomClaims;
import io.tavisco.rvstore.cars.exceptions.CarNotFoundException;
import io.tavisco.rvstore.cars.exceptions.CarNotOwnedException;
import io.tavisco.rvstore.cars.exceptions.UploadFailedException;
import io.tavisco.rvstore.cars.models.Car;
import io.tavisco.rvstore.cars.repository.CarRepository;
import io.tavisco.rvstore.cars.s3.CommonResource;
import io.tavisco.rvstore.cars.s3.FileObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.jwt.JsonWebToken;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

@Slf4j
@ApplicationScoped
@Transactional(REQUIRED)
public class CarService {
    
    @Inject
    CarRepository repository;

    @Inject
    S3Client s3;

    @Inject
    CommonResource commonResource;

    @Transactional(SUPPORTS)
    public Iterable<Car> findAllCars() {
        return repository.findAll();
    }

    @Transactional(SUPPORTS)
    public List<Car> findByName(final String name) {
        return repository.findByNameContaining(name);
    }

    public Car findById(final Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Car> findByUser(final JsonWebToken jwt) {
        if (jwt == null || StringUtils.isBlank(jwt.getClaim(JwtCustomClaims.UID.getText()))) {
            throw new RuntimeException("Null or invalid JWT!");
        }

        return repository.findByUploaderId(jwt.getClaim(JwtCustomClaims.UID.getText()));
    }

    public Car persistCar(@Valid final Car car) {
        return repository.save(car);
    }

    public Car persistUploadInfo(final FormData formData, final Long id, final JsonWebToken jwt) throws CarNotFoundException, CarNotOwnedException, UploadFailedException {
        Car car = findById(id);
        String userUid = jwt.getClaim(JwtCustomClaims.UID.getText());

        if (car == null) {
            throw new CarNotFoundException();
        } else if (!StringUtils.equalsIgnoreCase(car.uploaderId, userUid)) {
            log.info("Invalid owner! Car uploader ID: {}, Request JWT token ID: {}", car.uploaderId, jwt.getClaim(JwtCustomClaims.UID.getText()));
            throw new CarNotOwnedException();
        }

        PutObjectRequest putObjectRequest = commonResource.buildPutRequest(formData, userUid);

        PutObjectResponse putResponse = s3.putObject(putObjectRequest,
                RequestBody.fromFile(commonResource.uploadToTemp(formData.data)));

        if (putResponse == null) {
            throw new UploadFailedException();
        }

        car.uploadObjectKey = putObjectRequest.key();
        car.step = CarStep.DRAFT;

        log.info("Car file uploaded with key {}", car.uploadObjectKey);

        return persistCar(car);
    }

    public Response downloadCarFile(final String objectKey) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GetObjectResponse object = s3.getObject(commonResource.buildGetRequest(objectKey), ResponseTransformer.toOutputStream(baos));

        Response.ResponseBuilder response = Response.ok((StreamingOutput) baos::writeTo);
        response.header("Content-Disposition", "attachment;filename=" + objectKey);
        response.header("Content-Type", object.contentType());
        return response.build();
    }

    public List<FileObject> listFiles() {
        ListObjectsRequest listRequest = ListObjectsRequest.builder().bucket(commonResource.getBucketName()).build();

        //HEAD S3 objects to get metadata
        return s3.listObjects(listRequest).contents().stream().sorted(Comparator.comparing(S3Object::lastModified).reversed())
                .map(FileObject::from).collect(Collectors.toList());
    }

    // public void deleteCar(Long id) {
    //     Car car = Car.findById(id);
    //     car.delete();
    // }

}