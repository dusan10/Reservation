package com.jimmy.car.reservation.service;

import com.jimmy.car.reservation.model.InquiryForCar;
import com.jimmy.car.reservation.model.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationService {

    /**
     * Method returns all reservations
     *
     * @return all reservations from DB
     */
    List<Reservation> getAllReservations();

    /**
     * Method trying to reserve car for inquiry
     *
     * @param inquiryForCar  - customer create inquiry ror renting car
     *
     * @return Optional Reservation
     */
    Optional<Reservation> reserveCarForUse(InquiryForCar inquiryForCar);
}
