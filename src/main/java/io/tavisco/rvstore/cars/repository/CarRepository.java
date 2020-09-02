package io.tavisco.rvstore.cars.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import io.tavisco.rvstore.cars.models.Car;

public interface CarRepository extends CrudRepository<Car, Long> {
    List<Car> findByNameContaining(String name);
}