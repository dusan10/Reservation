package com.jimmy.car.reservation.service;

import java.security.NoSuchAlgorithmException;

public interface IdGeneratorService {

    /**
     * Method checks is car id valid per requirement
     *
     * @param carId - car id
     * @return true is car id valid
     */
    boolean isValidCarId(String carId);

    /**
     * Method checks is car id used in the system
     *
     * @param id - car id
     * @return true is car id is used
     */
    boolean isIdUsed(String id);

    /**
     * Method generates new id for car
     *
     * @return generated sting for car id by requirements
     */
    String generateNewId() throws NoSuchAlgorithmException;


}
