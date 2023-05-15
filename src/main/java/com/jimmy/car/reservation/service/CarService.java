package com.jimmy.car.reservation.service;

import com.jimmy.car.reservation.model.Car;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

public interface CarService {
    /**
     * Method adds a new car
     *
     * @param car - car
     * @return added Car
     */
    Car addCar(Car car) throws NoSuchAlgorithmException;

    /**
     * Method updates a car by id
     *
     * @param car - car
     * @return updated Car
     */
    Car updateCar(Car car);

    /**
     * Method removes a car by id
     *
     * @param id - id of car
     */
    void removeCar(String id);


    /**
     * Method finds all car
     *
     * @return List of all cars;
     */
    List<Car> getAllCars();

    /**
     * Method finds a car by id
     *
     * @param id - id of car
     * @return Optional car object
     */
    Optional<Car> getCarByIdentifier(String id);
}
