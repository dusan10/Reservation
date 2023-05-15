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
    private Car updateValuesCar;

    @BeforeEach
    void setUp() {
        originalCar = new Car("C12344321", "Zastava", "128");
        updateValuesCar = new Car("C12344321", "Zastava", "kec");
    }

    @Test
    @DisplayName("Add and update car")
    void addAndUpdateCar() throws NoSuchAlgorithmException {
        Car savedCar = carService.addCar(originalCar);
        Car updatedCar = carService.updateCar(updateValuesCar);
        Assertions.assertEquals(savedCar.getId(), updatedCar.getId());
        Assertions.assertEquals(savedCar.getProducer(), updatedCar.getProducer());
        Assertions.assertEquals("kec", updatedCar.getModel());

        carRepository.delete(updatedCar);
    }

    @Test
    @DisplayName("Add Car In Multi Thread Environment")
    void addCarInMultiThreadEnvironment() throws InterruptedException, ExecutionException, NoSuchAlgorithmException {
        int numThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Callable<Optional<Car>>> tasks = new ArrayList<>();

        // Create tasks for update car
        for (int i = 0; i < numThreads; i++) {
            originalCar.setId("C" +( 70000000 + i));
            LOGGER.warn(originalCar.getId());
            Callable<Optional<Car>> task = () -> Optional.ofNullable(carService.addCar(originalCar));
            tasks.add(task);
        }

        // Submit tasks to executor service
        List<Future<Optional<Car>>> futures = executorService.invokeAll(tasks);

        // Wait for all tasks to complete
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

       Assertions.assertEquals(103, carRepository.findAll().size());

    }
}
