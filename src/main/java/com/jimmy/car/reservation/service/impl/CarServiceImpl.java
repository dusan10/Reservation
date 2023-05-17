package com.jimmy.car.reservation.service.impl;

import com.jimmy.car.reservation.dao.CarRepository;
import com.jimmy.car.reservation.dao.ReservationRepository;
import com.jimmy.car.reservation.exceptions.CarBadValuesException;
import com.jimmy.car.reservation.exceptions.CarNotFoundException;
import com.jimmy.car.reservation.model.Car;
import com.jimmy.car.reservation.model.Reservation;
import com.jimmy.car.reservation.service.CarService;
import com.jimmy.car.reservation.service.IdGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
public class CarServiceImpl implements CarService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarServiceImpl.class);

    private final CarRepository carRepository;
    private final IdGeneratorService idGeneratorService;
    private final ReservationRepository reservationRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository,
                          IdGeneratorService idGeneratorService,
                          ReservationRepository reservationRepository) {
        this.carRepository = carRepository;
        this.idGeneratorService = idGeneratorService;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public synchronized Car addCar(Car car) throws NoSuchAlgorithmException {
        if (car.getId() != null) {
            throw new CarBadValuesException("Please do not use ID as attribute, system will generate ID of car by it self");
        }
        if (car.getModel() == null || car.getModel().isEmpty() || car.getProducer() == null || car.getProducer().isEmpty()) {
            LOGGER.warn("Car values are null or empty for car id {}", car.getId());
            throw new CarBadValuesException("Model or Producer attributes are null or empty");
        }
        car.setId(idGeneratorService.generateNewId());
        return carRepository.save(car);
    }

    @Override
    @Transactional
    public synchronized Car updateCar(Car car) {
        if (car.getId() == null){
            LOGGER.warn("Car values are null or empty for car id {}", car.getId());
            throw new CarNotFoundException("ID attribute can't be null");
        }
        Car savedCar = getCarByIdentifier(car.getId()).orElseThrow(() -> {
            LOGGER.error("Car with id {} not found", car.getId());
            return new CarNotFoundException("Car with id " + car.getId() + " not found");
        });
        savedCar.setProducer(car.getProducer());
        savedCar.setModel(car.getModel());
        return carRepository.save(savedCar);
    }

    @Override
    @Transactional
    public synchronized void removeCar(String id) {
        Car car = getCarByIdentifier(id).orElseThrow(() -> {
            LOGGER.error("Car with id {} not found", id);
            return new CarNotFoundException("Car with id " + id + " not found");
        });

        List<Reservation> createdReservationsWithCarThatWeWantDoDelete = reservationRepository.findAll()
                .stream()
                .filter(reservation -> reservation.getCarId().equals(car.getId()))
                .toList();

        reservationRepository.deleteAll(createdReservationsWithCarThatWeWantDoDelete);
        carRepository.delete(car);
    }

    @Override
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    @Override
    public Optional<Car> getCarByIdentifier(String id) {
        return carRepository.getCarByIdentifier(id);
    }
}
