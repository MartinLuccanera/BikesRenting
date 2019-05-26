package com.bikes.renting.model.impl;

import com.bikes.renting.model.iface.Rental;

import static com.bikes.renting.model.PricingConstants.WEEK_PRICE;

public class WeeklyRental implements Rental {

    @Override
    public double calculateRentalPricing(int timeUnits) {
        return timeUnits * WEEK_PRICE;
    }
}