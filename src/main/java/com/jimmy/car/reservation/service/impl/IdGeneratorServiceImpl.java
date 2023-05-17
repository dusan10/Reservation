package com.jimmy.car.reservation.service.impl;

import com.jimmy.car.reservation.dao.CarRepository;
import com.jimmy.car.reservation.model.Car;
import com.jimmy.car.reservation.service.IdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

@Service
public class IdGeneratorServiceImpl implements IdGeneratorService {

    private static final String CAR_CHARACTER_PROPERTY = "${cad.id.prefix.letter}";
    private static final String NUMBER_OF_DIGITS = "${cad.id.number.of.digits}";


    @Value(CAR_CHARACTER_PROPERTY)
    private String characterForCarId;

    @Value(NUMBER_OF_DIGITS)
    private int numberOfDigits;

    private final CarRepository carRepository;

    @Autowired
    public IdGeneratorServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public String generateNewId() throws NoSuchAlgorithmException {
        String id;
        do {
            id = generateId();
        } while (isIdUsed(id));
        return id;
    }

    private boolean isIdUsed(String id) {
        List<Car> allCars = carRepository.findAll();
        return allCars.stream().anyMatch(car -> car.getId().equals(id));
    }

    private String generateId() throws NoSuchAlgorithmException {
        int smallestNumber = (int) Math.pow(10, numberOfDigits - 1);
        int biggerNumber = (int) Math.pow(10, numberOfDigits) - 1;
        return characterForCarId + SecureRandom.getInstanceStrong().nextInt(smallestNumber, biggerNumber);
    }
}
