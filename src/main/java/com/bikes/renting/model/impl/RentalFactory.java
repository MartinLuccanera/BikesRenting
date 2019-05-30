package com.bikes.renting.model.impl;

import com.bikes.renting.model.iface.Rental;
import com.google.gson.JsonObject;

import static com.bikes.renting.model.RentalTypes.*;
import static com.bikes.renting.model.message_engine.JsonKeyConstants.NESTED_RENTALS_JSON_KEY;
import static com.bikes.renting.model.message_engine.JsonKeyConstants.RENTALS_QUANTITY_JSON_KEY;
import static com.bikes.renting.model.message_engine.JsonKeyConstants.RENTAL_TYPE_JSON_KEY;

/**
 * Factory class to create all the different kinds of rentals.
 */
public class RentalFactory {
    /**
     * Returns (if possible) an instantiated {@link Rental} object.
     *
     * @param rental {@link JsonObject} containing the data necessary to instantiate a rental.
     *
     * @return A functional Rental.
     */
    public static Rental createRental(JsonObject rental) {

        switch (rental.get(RENTAL_TYPE_JSON_KEY).toString()) {
            case RENTAL_TYPE_FAMILY:
                return new FamilyRental(rental.get(NESTED_RENTALS_JSON_KEY).getAsJsonArray());
            case RENTAL_TYPE_HOUR:
                return new HourlyRental(rental.get(RENTALS_QUANTITY_JSON_KEY).getAsInt());
            case RENTAL_TYPE_DAY:
                return new DailyRental(rental.get(RENTALS_QUANTITY_JSON_KEY).getAsInt());
            case RENTAL_TYPE_WEEK:
                return new WeeklyRental(rental.get(RENTALS_QUANTITY_JSON_KEY).getAsInt());
            default:
                throw new IllegalArgumentException("Invalid data on rental: " + rental.toString());
        }
    }
}
