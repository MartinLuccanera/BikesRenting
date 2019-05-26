package com.bikes.renting.model.impl;

import com.bikes.renting.model.iface.Rental;

import static com.bikes.renting.model.PricingConstants.HOUR_PRICE;

public class HourlyRental implements Rental {

    @Override
    public double calculateRentalPricing(int timeUnits) {
        return timeUnits * HOUR_PRICE;
    }
}