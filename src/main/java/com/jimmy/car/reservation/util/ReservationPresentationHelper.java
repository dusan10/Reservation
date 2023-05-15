package com.jimmy.car.reservation.util;

import com.jimmy.car.reservation.dao.CarRepository;
import com.jimmy.car.reservation.model.Reservation;
import com.jimmy.car.reservation.model.ReservationPresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReservationPresentationHelper {

    private final CarRepository carRepository;

    @Autowired
    public ReservationPresentationHelper(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<ReservationPresentation> populatePresentations(List<Reservation> reservations) {
        List<ReservationPresentation> result = new ArrayList<>();
        reservations.forEach(reservation -> {
            ReservationPresentation reservationPresentation = populateValuesForPresentation(reservation);
            result.add(reservationPresentation);
        });
        return result;
    }

    private ReservationPresentation populateValuesForPresentation(Reservation reservation) {
        ReservationPresentation reservationPresentation = new ReservationPresentation();
        reservationPresentation.setReservationStart(reservation.getReservationStart());
        reservationPresentation.setReservationEnd(reservation.getReservationEnd());
        reservationPresentation.setCar(carRepository.getCarByIdentifier(reservation.getCarId()).get());
        return reservationPresentation;
    }
}
