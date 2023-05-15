package com.jimmy.car.reservation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPresentation {
    private Car car;
    private LocalDateTime reservationStart;
    private LocalDateTime reservationEnd;
}
