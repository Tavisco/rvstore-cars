package io.tavisco.rvstore.cars;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;

import io.tavisco.rvstore.cars.enums.JwtCustomClaims;
import io.tavisco.rvstore.cars.models.Car;
import io.tavisco.rvstore.cars.repository.CarRepository;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.jwt.JsonWebToken;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

import java.util.List;

@ApplicationScoped
@Transactional(REQUIRED)
public class CarService {
    
    @Inject
    CarRepository repository;

    @Transactional(SUPPORTS)
    public Iterable<Car> findAllCars() {
        return repository.findAll();
    }

    @Transactional(SUPPORTS)
    public List<Car> findByName(String name) {
        return repository.findByNameContaining(name);
    }

    public Car findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Car> findByUser(JsonWebToken jwt) {
        if (jwt == null || StringUtils.isBlank(jwt.getClaim(JwtCustomClaims.UID.getText()))) {
            throw new RuntimeException("Null or invalid JWT!");
        }

        return repository.findByUploaderId(jwt.getClaim(JwtCustomClaims.UID.getText()));
    }

    public Car persistCar(@Valid Car car) {
        repository.save(car);
        return car;
    }

    // public void deleteCar(Long id) {
    //     Car car = Car.findById(id);
    //     car.delete();
    // }

}