package com.bikes.renting.model;

import com.google.common.collect.ImmutableList;

/**
 * Helper class to keep rental types / kafka topics.
 */
public class RentalTypes {
    /**
     * List of composed topic types for kafka. (Example: family type of rental)
     */
    public static final ImmutableList<String> COMPOSED_TOPICS = ImmutableList.of("family");

    /**
     * List of topics for kafka (kinds of rental).
     */
    public static final ImmutableList<String> TOPICS = ImmutableList.of("hour","day","week",
            String.join(",", COMPOSED_TOPICS));
}
