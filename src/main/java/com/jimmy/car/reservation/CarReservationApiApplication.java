package com.jimmy.car.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.jimmy.car.reservation")
@EntityScan(basePackages = "com.jimmy.car.reservation")
@SpringBootApplication
public class CarReservationApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarReservationApiApplication.class, args);
	}
}
