package com.bikes.renting.model;

import com.google.common.collect.ImmutableList;

/**
 * Helper class to keep rental types / kafka topics.
 */
public class RentalTypes {

    public static final String RENTAL_TYPE_FAMILY = "family";
    public static final String RENTAL_TYPE_HOUR = "hour";
    public static final String RENTAL_TYPE_DAY = "day";
    public static final String RENTAL_TYPE_WEEK = "week";
    /**
     * List of composed topic types for kafka. (Example: family type of rental)
     */
    public static final ImmutableList<String> COMPOSED_TOPICS = ImmutableList.of(RENTAL_TYPE_FAMILY);

    /**
     * List of topics for kafka (kinds of rental).
     */
    public static final ImmutableList<String> TOPICS = ImmutableList.of(RENTAL_TYPE_HOUR,RENTAL_TYPE_DAY,RENTAL_TYPE_WEEK,
            String.join(",", COMPOSED_TOPICS));
}
