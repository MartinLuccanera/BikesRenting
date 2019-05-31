package com.bikes.renting.controller;

import com.google.gson.JsonParser;
import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.bikes.renting.model.RentalTypes.*;

public class KafkaUtilsTest {
    private String jsonTestString5Rentals = "{\"topic\": \"family\",\"rentals\": [{ \"topic\": \"day\", \"units\": 1},{ \"topic\": \"week\", \"units\": 2},{ \"topic\": \"day\", \"units\": 3},{ \"topic\": \"hour\", \"units\": 4},{ \"topic\": \"day\", \"units\": 5}] }";
    private String jsonTestStringSingleRental = "{ \"topic\": \"day\", \"units\": 1}";

    @Test
    public void validateAssembleMessageForKafkaComposed(){
        List<String> ListTestStringComposedRentalTopic = new ArrayList<>(
                Arrays.asList(
                        RENTAL_TYPE_DAY,
                        RENTAL_TYPE_WEEK,
                        RENTAL_TYPE_DAY,
                        RENTAL_TYPE_HOUR,
                        RENTAL_TYPE_DAY
                ));
        List<Integer> ListTestStringComposedRentalQuantity = new ArrayList<>(
                Arrays.asList(1,2,3,4,5));

        JsonAssert.assertJsonEquals(KafkaUtils.assembleMessage(
                RENTAL_TYPE_FAMILY,
                ListTestStringComposedRentalTopic,
                ListTestStringComposedRentalQuantity
                ),
                new JsonParser().parse(jsonTestString5Rentals).getAsJsonObject()
        );
    }

    @Test
    public void validateAssembleMessageForKafkaAtomic(){
        JsonAssert.assertJsonEquals(KafkaUtils.assembleMessage(
                RENTAL_TYPE_DAY,
                1
                ),
                new JsonParser().parse(jsonTestStringSingleRental).getAsJsonObject()
        );
    }

}
