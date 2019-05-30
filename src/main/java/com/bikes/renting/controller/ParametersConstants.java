package com.bikes.renting.controller;

public class ParametersConstants {
    /**
     * Represents a type of rental.
     * Parameter requested by rental API {@link RentalController}.
     * Values for this key are {@link com.bikes.renting.model.RentalTypes}
     */
    public static final String RENTAL_TYPE_PARAMETER_KEY = "rentalType";

    /**
     * Represents a newsted type of rental.
     * Parameter requested by rental API {@link RentalController}.
     * Values for this key are {@link com.bikes.renting.model.RentalTypes}
     */
    public static final String NESTED_RENTALS_PARAMETER_KEY = "subRental";

    /**
     * Represents amount of pairing rentals that will be taken.
     * Parameter requested by rental API {@link RentalController}.
     * Values for this key are {@link com.bikes.renting.model.RentalTypes}
     */
    public static final String RENTALS_QUANTITY_PARAMETER_KEY = "quantity";
}
