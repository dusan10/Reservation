package com.jimmy.car.reservation.service;

import java.security.NoSuchAlgorithmException;

public interface IdGeneratorService {

    /**
     * Method generates new id for car
     *
     * @return generated sting for car id by requirements
     */
    String generateNewId() throws NoSuchAlgorithmException;
}
