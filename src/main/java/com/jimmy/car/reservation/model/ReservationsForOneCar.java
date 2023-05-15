package com.jimmy.car.reservation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationsForOneCar {
    private Car car;
    private List<Reservation> reservations;
}
