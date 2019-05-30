package com.bikes.renting.model.impl;

import com.bikes.renting.model.iface.Rental;

import static com.bikes.renting.model.PricingConstants.HOUR_PRICE;

/**
 * <p>Rental implementation for Hourly rentals.</p>
 */
public class HourlyRental implements Rental {
    private int timeUnits;

    HourlyRental(int timeUnits) {
        this.timeUnits = timeUnits;
    }

    @Override
    public double calculateRentalPricing() {
        return this.timeUnits * HOUR_PRICE;
    }
}