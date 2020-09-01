package io.tavisco.rvstore.cars;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;

import io.tavisco.rvstore.cars.models.Car;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

import java.util.List;

@ApplicationScoped
@Transactional(REQUIRED)
public class CarService {
    
    @Transactional(SUPPORTS)
    public List<Car> findAllCars() {
        return Car.listAll();
    }

    @Transactional(SUPPORTS)
    public List<Car> findByName(String name) {
        return Car.findByName(name);
    }

    public Car findById(Long id) {
        return Car.findById(id);
    }

    public Car persistCar(@Valid Car car) {
        Car.persist(car);
        return car;
    }

    public void deleteCar(Long id) {
        Car car = Car.findById(id);
        car.delete();
    }

}