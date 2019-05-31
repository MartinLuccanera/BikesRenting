package com.bikes.renting.controller;

import com.bikes.renting.model.iface.Rental;
import com.bikes.renting.model.impl.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static com.bikes.renting.model.PricingConstants.FAMILY_DISCOUNT;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.bikes.renting.model.message_engine.JsonKeyConstants.NESTED_RENTALS_JSON_KEY;
import static org.hamcrest.CoreMatchers.equalTo;

public class FamilyRentalTest {
    private String jsonTestString = "{\"topic\": \"family\",\"rentals\": [{ \"topic\": \"day\", \"units\": 1},{ \"topic\": \"week\", \"units\": 2},{ \"topic\": \"day\", \"units\": 3},{ \"topic\": \"hour\", \"units\": 4},{ \"topic\": \"day\", \"units\": 5}] }";
    private FamilyRental familyRental;

    @Before
    public void setUp() {
        JsonObject jo = new JsonParser().
                parse(jsonTestString)
                .getAsJsonObject();
        familyRental = new FamilyRental(jo.get(NESTED_RENTALS_JSON_KEY).getAsJsonArray());
    }

    /**
     * Calculates cost of rental in 2 different ways and then compares them.
     */
    @Test
    public void sumUsingComposite_thenIndividualCalculations(){
        // familyRental.calculateRentalPricing() instantiates all members of a composed rental and applies discount.
        //We assert that against individual calculations.
        assertThat(familyRental.calculateRentalPricing(), equalTo(totalSum() * FAMILY_DISCOUNT));
    }

    /**
     * Instantiates several atomic rentals and adds them up.
     *
     * @return Sum of all individual rentals
     */
    private double totalSum() {
        double result = 0;

        JsonParser jp = new JsonParser();
        List<JsonObject> atomicRentals = new ArrayList<>();
        atomicRentals.add(jp.parse("{\"topic\": \"day\", \"units\": 1}").getAsJsonObject());
        atomicRentals.add(jp.parse("{\"topic\": \"week\", \"units\": 2}").getAsJsonObject());
        atomicRentals.add(jp.parse("{\"topic\": \"day\", \"units\": 3}").getAsJsonObject());
        atomicRentals.add(jp.parse("{\"topic\": \"hour\", \"units\": 4}").getAsJsonObject());
        atomicRentals.add(jp.parse("{\"topic\": \"day\", \"units\": 5}").getAsJsonObject());

        for (JsonObject jo : atomicRentals) {
            Rental rental = RentalFactory.createRental(jo);
            result = result + rental.calculateRentalPricing();
        }
        return result;
    }
}
