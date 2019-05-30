package com.bikes.renting.controller;

import com.bikes.renting.model.iface.Rental;
import com.bikes.renting.model.impl.RentalFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;

import static com.bikes.renting.model.PricingConstants.FAMILY_MAX_SIZE;
import static com.bikes.renting.model.PricingConstants.FAMILY_MIN_SIZE;
import static com.bikes.renting.model.RentalTypes.RENTAL_TYPE_FAMILY;
import static com.bikes.renting.model.message_engine.JsonKeyConstants.NESTED_RENTALS_JSON_KEY;
import static com.bikes.renting.model.message_engine.JsonKeyConstants.RENTAL_TYPE_JSON_KEY;

public class RentalUtils {
    private static final Logger logger = Logger.getLogger(RentalUtils.class);

    /**
     * Runs validations on payload received from API endpoint {@link RentalController}.
     * If anything fails, execution is stopped and exception is raised.
     *
     * @param message The result of an API request.
     */
    public static void validateRentalParams(JsonObject message) {
        // Checking for validity of parameters sent through API.

        // It's not a single rental nor a composed rental.
        if (message.get(RENTAL_TYPE_JSON_KEY) == null || message.get(NESTED_RENTALS_JSON_KEY) == null) {
            String error = "Payload is missing mandatory field." + message.toString();
            logger.error(error);
            throw new RuntimeException(error);
        }

        // Here we will handle single rentals.
        if (!message.get(RENTAL_TYPE_JSON_KEY).getAsString().equals(RENTAL_TYPE_FAMILY)) {
            //TODO: Should really check for rental types being only atomic and not composed.
            String msg = "Payload is not composite rental." + message.toString();
            logger.info(msg);

            Rental rental = RentalFactory.createRental(message);
            logger.info("Cost of rental: " + rental.calculateRentalPricing());
        }

        // Going forward we will handle composed rentals.
        JsonArray rentals = message.getAsJsonArray(NESTED_RENTALS_JSON_KEY);
        //TODO: Should check for nested composed rental-types.

        // Composed rentals have min and max quantities.
        if (rentals.size() > FAMILY_MAX_SIZE || rentals.size() < FAMILY_MIN_SIZE) {
            String error = "Payload has an incorrect number of rentals." + message.toString();
            logger.error(error);
            throw new RuntimeException(error);
        }

        //if we got to this point, then we are dealing with a properly formatted Json to calculate
        // a composed type of rental.

        String msg = "Payload is a composite rental." + message.toString();
        logger.info(msg);
    }
}
