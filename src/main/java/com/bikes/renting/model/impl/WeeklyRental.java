package com.bikes.renting.model.impl;

import com.bikes.renting.model.iface.Rental;

import static com.bikes.renting.model.PricingConstants.WEEK_PRICE;

/**
 * <p>Rental implementation for Weekly rentals.</p>
 */
public class WeeklyRental implements Rental {
    private int timeUnits;

    WeeklyRental(int timeUnits) {
        this.timeUnits = timeUnits;
    }

    @Override
    public double calculateRentalPricing() {
        return this.timeUnits * WEEK_PRICE;
    }
}