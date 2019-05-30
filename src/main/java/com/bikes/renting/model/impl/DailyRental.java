package com.bikes.renting.model.impl;

import com.bikes.renting.model.iface.Rental;
import static com.bikes.renting.model.PricingConstants.DAY_PRICE;

/**
 * <p>Rental implementation for Daily rentals.</p>
 */
public class DailyRental implements Rental {
    private int timeUnits;

    DailyRental(int timeUnits) {
        this.timeUnits = timeUnits;
    }

    @Override
    public double calculateRentalPricing() {
        return timeUnits * DAY_PRICE;
    }
}