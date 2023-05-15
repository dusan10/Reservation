package com.jimmy.car.reservation.service.impl;

import com.jimmy.car.reservation.dao.ReservationRepository;
import com.jimmy.car.reservation.model.Car;
import com.jimmy.car.reservation.model.InquiryForCar;
import com.jimmy.car.reservation.model.Reservation;
import com.jimmy.car.reservation.model.ReservationsForOneCar;
import com.jimmy.car.reservation.service.CarService;
import com.jimmy.car.reservation.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ReservationServiceImpl implements ReservationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final ReservationRepository reservationRepository;
    private final CarService carService;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  CarService carService) {
        this.reservationRepository = reservationRepository;
        this.carService = carService;
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public synchronized Optional<Reservation> reserveCarForUse(InquiryForCar inquiryForCar) {
        if (!isInquiryValid(inquiryForCar)) {
            LOGGER.error("Inquiry for car was wrong, you can create only inquiry for next 24h, and start date needs to be before ends date");
            return Optional.empty();
        }
        return makeReservation(inquiryForCar);
    }

    private Optional<Reservation> makeReservation(InquiryForCar inquiryForCar) {
        List<Reservation> reservations = getAllReservations();
        List<Car> allCars = carService.getAllCars();

        List<ReservationsForOneCar> reservationsForOneCarList = new ArrayList<>();
        allCars.forEach(car -> {
            ReservationsForOneCar reservationsForOneCar = new ReservationsForOneCar();
            reservationsForOneCar.setCar(car);
            List<Reservation> reservationListForOneCar =
                    reservations.stream().filter(res -> res.getCarId().equals(car.getId())).toList();
            reservationsForOneCar.setReservations(reservationListForOneCar);
            reservationsForOneCarList.add(reservationsForOneCar);
        });

        Set<Car> availableCars = new HashSet<>();
        reservationsForOneCarList.forEach(res -> {
            if (isCarAvailableToday(res, inquiryForCar)) {
                availableCars.add(res.getCar());
            }
        });

        if (availableCars.isEmpty()) {
            return Optional.empty();
        }

        Car selectedCar = availableCars.stream().findAny().get();
        Reservation newReservation = new Reservation();
        newReservation.setReservationStart(inquiryForCar.getStart());
        newReservation.setReservationEnd(inquiryForCar.getStart().plusMinutes(inquiryForCar.getDuration()));
        newReservation.setCarId(selectedCar.getId());
        return Optional.of(reservationRepository.save(newReservation));
    }

    private boolean isCarAvailableToday(ReservationsForOneCar res, InquiryForCar inquiryForCar) {
        AtomicBoolean available = new AtomicBoolean(true);
        List<Reservation> reservations = res.getReservations();
        reservations.forEach(reservation -> available.set(available.get() && isAvailableForOneReservation(reservation, inquiryForCar)));
        return available.get();
    }

    private boolean isAvailableForOneReservation(Reservation reservation, InquiryForCar inquiryForCar) {
        LocalDateTime start = inquiryForCar.getStart();
        LocalDateTime end = start.plusMinutes(inquiryForCar.getDuration());
        return (start.isBefore(reservation.getReservationStart()) && end.isBefore(reservation.getReservationStart())) ||
                (start.isAfter(reservation.getReservationEnd()) && end.isAfter(reservation.getReservationEnd()));
    }

    private boolean isInquiryValid(InquiryForCar inquiryForCar) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayAhead = now.plusHours(24);
        LocalDateTime start = inquiryForCar.getStart();
        LocalDateTime end = start.plusMinutes(inquiryForCar.getDuration());
        return inquiryForCar.getDuration() >= 30 && inquiryForCar.getDuration() <= 120 &&
                start != null && end != null &&
                !start.isAfter(end) &&
                !start.isBefore(now) && !end.isBefore(now) &&
                !start.isAfter(oneDayAhead) && !end.isAfter(oneDayAhead.plusHours(2));
    }
}
