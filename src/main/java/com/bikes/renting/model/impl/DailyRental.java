package com.bikes.renting.model.impl;

import com.bikes.renting.model.iface.Rental;
import static com.bikes.renting.model.PricingConstants.DAY_PRICE;

public class DailyRental implements Rental {

    @Override
    public double calculateRentalPricing(int timeUnits) {
        return timeUnits * DAY_PRICE;
    }
}