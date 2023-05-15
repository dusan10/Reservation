package com.jimmy.car.reservation.controllers;

import com.jimmy.car.reservation.model.Car;
import com.jimmy.car.reservation.model.InquiryForCar;
import com.jimmy.car.reservation.model.Reservation;
import com.jimmy.car.reservation.model.ReservationPresentation;
import com.jimmy.car.reservation.service.CarService;
import com.jimmy.car.reservation.service.ReservationService;
import com.jimmy.car.reservation.util.ReservationPresentationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class CarReservationController {

    private final ReservationService reservationService;
    private final CarService carService;
    private final ReservationPresentationHelper reservationPresentationHelper;

    @Autowired
    public CarReservationController(ReservationService reservationService,
                                    CarService carService,
                                    ReservationPresentationHelper reservationPresentationHelper) {
        this.reservationService = reservationService;
        this.carService = carService;
        this.reservationPresentationHelper = reservationPresentationHelper;
    }

    @GetMapping
    public ResponseEntity<List<ReservationPresentation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservationPresentationHelper.populatePresentations(reservations));
    }

    @PostMapping
    public ResponseEntity<?> reserveCarForUse(@RequestBody InquiryForCar inquiryForCar) {
        Optional<Reservation> createdReservation = reservationService.reserveCarForUse(inquiryForCar);
        return createdReservation.isPresent() ?
                ResponseEntity.ok(getReservationPresentationForCustomer(createdReservation)) :
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Reservation is not created, Inquiry have some problems, please check documentation.");

    }

    private ReservationPresentation getReservationPresentationForCustomer(Optional<Reservation> createdReservation) {
        Reservation reservation = createdReservation.get();
        ReservationPresentation result = new ReservationPresentation();
        result.setReservationStart(reservation.getReservationStart());
        result.setReservationEnd(reservation.getReservationEnd());
        Car car = carService.getCarByIdentifier(reservation.getCarId()).get();
        result.setCar(car);
        return result;
    }
}
