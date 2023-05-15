package com.jimmy.car.reservation.service.impl;

import com.jimmy.car.reservation.dao.ReservationRepository;
import com.jimmy.car.reservation.model.InquiryForCar;
import com.jimmy.car.reservation.model.Reservation;
import com.jimmy.car.reservation.service.ReservationService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@SpringBootTest
@Transactional
class ReservationServiceImpMultithreadingEnvironmentIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("Reserve car method and service test in multithreading environment")
    void reserveCarForUseTestInMultithreadingEnvironment() throws InterruptedException, ExecutionException {
        LocalDateTime start = LocalDateTime.now().plusHours(5);
        int duration = 60;
        InquiryForCar inquiryForCar = new InquiryForCar(new Random().nextLong(), start, duration);

        int numThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Callable<Optional<Reservation>>> tasks = new ArrayList<>();

        // Create tasks for making reservations
        for (int i = 0; i < numThreads; i++) {
            Callable<Optional<Reservation>> task = () -> reservationService.reserveCarForUse(inquiryForCar);
            tasks.add(task);
        }

        // Submit tasks to executor service
        List<Future<Optional<Reservation>>> futures = executorService.invokeAll(tasks);

        // Wait for all tasks to complete
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        // Check results
        int numSuccessfulReservations = 0;
        List<UUID> newReservationIdList = new ArrayList<>();
        for (Future<Optional<Reservation>> future : futures) {
            Optional<Reservation> result = future.get();
            if (result.isPresent()) {
                numSuccessfulReservations++;
                newReservationIdList.add(result.get().getId());
            }
        }

        Assertions.assertEquals(2, numSuccessfulReservations);
        Assertions.assertEquals(7, reservationService.getAllReservations().size());

        List<Reservation> allReservations = reservationRepository.findAll();
        newReservationIdList.forEach(newReservationId -> {
            Reservation reservationForDelete =
                    allReservations.stream().filter(res -> res.getId().equals(newReservationId)).findAny().get();
            reservationRepository.delete(reservationForDelete);
        });
    }
}
