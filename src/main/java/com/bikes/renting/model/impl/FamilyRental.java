package com.bikes.renting.model.impl;

import com.bikes.renting.model.iface.Rental;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static com.bikes.renting.model.PricingConstants.FAMILY_DISCOUNT;
import static com.bikes.renting.model.message_engine.JsonKeyConstants.RENTAL_TYPE_JSON_KEY;

/**
 * <p>Rental implementation for family-type of rentals.</p>
 */
public class FamilyRental implements Rental {
    private List<Rental> rentals = new ArrayList<>();

    public FamilyRental(JsonArray rentals) {
        for (JsonElement rental : rentals) {
            JsonObject rentalObj = rental.getAsJsonObject();
            this.rentals.add(
                    RentalFactory.createRental(rentalObj)
            );
            rentalObj.get(RENTAL_TYPE_JSON_KEY);
        }
    }

    @Override
    public double calculateRentalPricing() {
        double result = 0;
        for(Rental rental : this.rentals) {
            result = result + rental.calculateRentalPricing();
        }
        return result * FAMILY_DISCOUNT;
    }
}