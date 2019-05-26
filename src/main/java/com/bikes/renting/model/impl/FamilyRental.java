package com.bikes.renting.model.impl;

import com.bikes.renting.model.iface.Rental;

import java.util.List;

import static com.bikes.renting.model.PricingConstants.FAMILY_DISCOUNT;

public class FamilyRental implements Rental {
    private List<Rental> rentals;
    //TODO Implement calculation for family rental. How to instantiate? How to calculate?

    @Override
    public double calculateRentalPricing(int timeUnits) {
        return timeUnits * //TODO sum all the objects on the list
        FAMILY_DISCOUNT;
    }
}