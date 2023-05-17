package com.jimmy.car.reservation.service.impl;

import com.jimmy.car.reservation.dao.CarRepository;
import com.jimmy.car.reservation.exceptions.CarBadValuesException;
import com.jimmy.car.reservation.model.Car;
import com.jimmy.car.reservation.service.IdGeneratorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    private final CarRepository carRepository = Mockito.mock(CarRepository.class);
    private final IdGeneratorService idGeneratorService = Mockito.mock(IdGeneratorService.class);

    @InjectMocks
    private CarServiceImpl carService;

    private Car car;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        car = new Car();
        car.setId(null);
        car.setProducer("Toyota");
        car.setModel("Camry");
    }

    @Test
    @DisplayName("Add car test with inputted id")
    void addCarTest() throws NoSuchAlgorithmException {
        Mockito.when(carRepository.findAll()).thenReturn(Collections.emptyList());
        Mockito.when(idGeneratorService.generateNewId()).thenReturn("generatedValue");
        Mockito.when(carRepository.save(car)).thenReturn(car);

        Car savedCar = carService.addCar(car);

        Assertions.assertEquals("generatedValue", savedCar.getId());
        Assertions.assertEquals("Toyota", savedCar.getProducer());
        Assertions.assertEquals("Camry", savedCar.getModel());
    }

    @Test
    @DisplayName("Add car test with model null and model empty string")
    void addCarTestNoId() {
        car.setModel(null);
        Mockito.when(carRepository.save(Mockito.any())).thenReturn(car);
        assertThrows(CarBadValuesException.class, () -> carService.addCar(car));
        car.setModel("");
        assertThrows(CarBadValuesException.class, () -> carService.addCar(car));
    }

    @Test
    @DisplayName("Add car test with model null and model empty string")
    void addCarTestWithId() {
        car.setId("C12311452");
        assertThrows(CarBadValuesException.class, () -> carService.addCar(car));
    }
}
