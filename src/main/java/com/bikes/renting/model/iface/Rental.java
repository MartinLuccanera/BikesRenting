package com.bikes.renting.model.iface;

/**
 * <p>Rental defines an interface to calculate the cost of a rental type.</p>
 */
public interface Rental{

    /**
     * <p>Calculates the cost of the rental.</p>
     *
     * @return Total cost of rental.
     */
    double calculateRentalPricing();
}