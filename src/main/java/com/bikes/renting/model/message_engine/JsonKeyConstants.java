package com.bikes.renting.model.message_engine;

/**
 * Helper class to keep all json keys in one place.
 */
public class JsonKeyConstants {

    /**
     * Key used to extract rental type (and kafka topic) from incoming parameters in JSON objects.
     */
    public static final String RENTAL_TYPE_JSON_KEY = "topic";

    /**
     * Key used to extract rentals list when we are dealing with a composed rental in JSON objects.
     */
    public static final String NESTED_RENTALS_JSON_KEY = "rentals";

    /**
     * Key used to extract units of rentals from incoming parameters in JSON objects.
     */
    public static final String RENTALS_QUANTITY_JSON_KEY = "units";
}
