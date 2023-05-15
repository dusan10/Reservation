package com.jimmy.car.reservation.dao;

import com.jimmy.car.reservation.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    /**
     * Method gets a car by id
     *
     * @param id -  Id of car
     * @return Car
     */

    @Query("SELECT c FROM Car c WHERE c.id = :id")
    Optional<Car> getCarByIdentifier(String id);
}
