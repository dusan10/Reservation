package com.jimmy.car.reservation.service.impl;

import com.jimmy.car.reservation.dao.ReservationRepository;
import com.jimmy.car.reservation.model.Car;
import com.jimmy.car.reservation.model.InquiryForCar;
import com.jimmy.car.reservation.model.Reservation;
import com.jimmy.car.reservation.service.CarService;
import com.jimmy.car.reservation.service.ReservationService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

@SpringBootTest
@Transactional
class ReservationServiceImplIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CarService carService;

    @Test
    @DisplayName("Reserve car test real example with good inquiry")
    void reserveCarForUseTest() {
        LocalDateTime start = LocalDateTime.now().plusHours(7);
        int duration = 40;
        InquiryForCar inquiryForCar = new InquiryForCar(new Random().nextLong(), start, duration);

        Optional<Reservation> result = reservationService.reserveCarForUse(inquiryForCar);

        Assertions.assertTrue(result.isPresent());
        Reservation reservation = result.get();
        Assertions.assertNotNull(reservation.getId());
        Assertions.assertEquals(start, reservation.getReservationStart());
        Assertions.assertEquals(start.plusMinutes(duration), reservation.getReservationEnd());
        Assertions.assertNotNull(reservation.getCarId());

        reservationRepository.delete(result.get());
    }

    @Test
    @DisplayName("Reserve car test real example with duration smaller than 30min")
    void reserveCarForUseTestWithShortDuration() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        int duration = 10;
        InquiryForCar inquiryForCar = new InquiryForCar(new Random().nextLong(), start, duration);
        Optional<Reservation> result = reservationService.reserveCarForUse(inquiryForCar);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Reserve car test - 3 additional car and 3 inquiry")
    void reserveCarForUseTestThreeInquiry() {
        LocalDateTime start = LocalDateTime.now().plusHours(5);
        int duration = 30;

        List<String> carIds = new ArrayList<>();
        IntStream.range(0, 3)
                .forEach(i -> {
                    Car car = (new Car("TestProducer", "TestModel"));
                    try {
                        carService.addCar(car);
                        carIds.add(car.getId());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                });

        InquiryForCar inquiryForCar1 = new InquiryForCar(new Random().nextLong(), start, duration);
        InquiryForCar inquiryForCar2 = new InquiryForCar(new Random().nextLong(), start, duration);
        InquiryForCar inquiryForCar3 = new InquiryForCar(new Random().nextLong(), start, duration);

        Optional<Reservation> result1 = reservationService.reserveCarForUse(inquiryForCar1);
        Assertions.assertTrue(result1.isPresent());
        Optional<Reservation> result2 = reservationService.reserveCarForUse(inquiryForCar2);
        Assertions.assertTrue(result2.isPresent());
        Optional<Reservation> result3 = reservationService.reserveCarForUse(inquiryForCar3);
        Assertions.assertTrue(result3.isPresent());

        reservationRepository.delete(result1.get());
        reservationRepository.delete(result2.get());
        carIds.forEach(id -> carService.removeCar(id));
    }
}
