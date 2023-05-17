package com.jimmy.car.reservation.controllers;

import com.jimmy.car.reservation.exceptions.CarBadValuesException;
import com.jimmy.car.reservation.exceptions.CarNotFoundException;
import com.jimmy.car.reservation.model.Car;
import com.jimmy.car.reservation.service.CarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cars")
public class CarApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarApiController.class);

    private final CarService carService;

    @Autowired
    public CarApiController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        List<Car> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    @PostMapping
    public ResponseEntity<?> addCar(@RequestBody Car car) throws NoSuchAlgorithmException {
        Car createdCar;
        try {
            createdCar = carService.addCar(car);
        } catch (CarBadValuesException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return createdCar.getId().equals(car.getId()) ?
                ResponseEntity.ok(createdCar) :
                ResponseEntity.status(HttpStatus.CREATED)
                        .body("Car is created, but because id you use was not by standards, system generates one for you");
    }

    @PutMapping
    public ResponseEntity<?> updateCar(@RequestBody Car car) {
        try {
            Car updatedCar = carService.updateCar(car);
            return ResponseEntity.ok(updatedCar);
        } catch (CarNotFoundException ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car with that id value was not found");
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeCar(@PathVariable String id) {
        try {
            carService.removeCar(id);
            return ResponseEntity.noContent().build();
        } catch (CarNotFoundException ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car with that id value was not found");
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCarByIdentifier(@PathVariable String id) {
        Optional<Car> car = carService.getCarByIdentifier(id);
        if (car.isPresent()) {
            return ResponseEntity.ok(car.get());
        } else {
            LOGGER.error("Car is not found for id {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car with that id value was not found");
        }
    }
}
