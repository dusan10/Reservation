package com.jimmy.car.reservation.service.impl;

import com.jimmy.car.reservation.dao.CarRepository;
import com.jimmy.car.reservation.dao.ReservationRepository;
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
    private final ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);

    @InjectMocks
    private CarServiceImpl carService;

    private Car car;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        car = new Car();
        car.setId("C12345");
        car.setProducer("Toyota");
        car.setModel("Camry");
    }

    @Test
    @DisplayName("Add car test with invalid id")
    void addCarTestNotValidId() throws NoSuchAlgorithmException {
        Mockito.when(idGeneratorService.isValidCarId("C12345")).thenReturn(false);
        Mockito.when(carRepository.findAll()).thenReturn(Collections.emptyList());
        Mockito.when(idGeneratorService.generateNewId()).thenReturn("generatedValue");
        Mockito.when(carRepository.save(car)).thenReturn(car);

        Car savedCar = carService.addCar(car);

        Assertions.assertEquals("generatedValue", savedCar.getId());
        Assertions.assertEquals("Toyota", savedCar.getProducer());
        Assertions.assertEquals("Camry", savedCar.getModel());
    }

    @Test
    @DisplayName("Add car test with valid id")
    void addCarTestValidId() throws NoSuchAlgorithmException {
        Mockito.when(idGeneratorService.isValidCarId(Mockito.any())).thenReturn(true);
        Mockito.when(carRepository.save(Mockito.any())).thenReturn(car);
        Car savedCar = carService.addCar(car);
        Assertions.assertEquals(car, savedCar);
        Mockito.verify(idGeneratorService, Mockito.never()).generateNewId();
        Mockito.verify(carRepository, Mockito.times(1)).save(car);
    }

    @Test
    @DisplayName("Add car test with model null and model empty string")
    void addCarTestNoId() throws NoSuchAlgorithmException {
        car.setModel(null);
        Mockito.when(idGeneratorService.isValidCarId(Mockito.any())).thenReturn(true);
        Mockito.when(carRepository.save(Mockito.any())).thenReturn(car);
        assertThrows(CarBadValuesException.class, () -> carService.addCar(car));
        car.setModel("");
        assertThrows(CarBadValuesException.class, () -> carService.addCar(car));
    }
}
