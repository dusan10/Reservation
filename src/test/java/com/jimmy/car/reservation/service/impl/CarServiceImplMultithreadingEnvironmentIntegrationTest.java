package com.jimmy.car.reservation.service.impl;

import com.jimmy.car.reservation.dao.CarRepository;
import com.jimmy.car.reservation.dao.ReservationRepository;
import com.jimmy.car.reservation.exceptions.CarBadValuesException;
import com.jimmy.car.reservation.model.Car;
import com.jimmy.car.reservation.model.InquiryForCar;
import com.jimmy.car.reservation.model.Reservation;
import com.jimmy.car.reservation.service.CarService;
import com.jimmy.car.reservation.service.IdGeneratorService;
import com.jimmy.car.reservation.service.ReservationService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CarServiceImplMultithreadingEnvironmentIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarServiceImplMultithreadingEnvironmentIntegrationTest.class);

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarService carService;

    private Car originalCar;

    @BeforeEach
    void setUp() {
        originalCar = new Car("Zastava", "128");
    }

    @Test
    @DisplayName("Add and update car")
    void addAndUpdateCar() throws NoSuchAlgorithmException {
        Car savedCar = carService.addCar(originalCar);
        Car updateValuesCar = new Car(savedCar.getId(), "Zastava", "kec");
        Car updatedCar = carService.updateCar(updateValuesCar);
        Assertions.assertEquals(savedCar.getId(), updatedCar.getId());
        Assertions.assertEquals(savedCar.getProducer(), updatedCar.getProducer());
        Assertions.assertEquals("kec", updatedCar.getModel());
    }

    @Test
    @DisplayName("delete saved car")
    void deleteAddedCar() throws NoSuchAlgorithmException {
        Car savedCar = carService.addCar(originalCar);
        carService.removeCar(savedCar.getId());
        Assertions.assertEquals(carRepository.getCarByIdentifier(savedCar.getId()), Optional.empty());
    }
}
