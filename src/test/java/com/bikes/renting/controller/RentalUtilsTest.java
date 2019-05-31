package com.bikes.renting.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RentalUtilsTest {
    private String jsonTestString2Rentals = "{\"topic\": \"family\",\"rentals\": [{ \"topic\": \"day\", \"units\": 1},{ \"topic\": \"week\", \"units\": 2}] }";
    private String jsonTestString5Rentals = "{\"topic\": \"family\",\"rentals\": [{ \"topic\": \"day\", \"units\": 1},{ \"topic\": \"week\", \"units\": 2},{ \"topic\": \"day\", \"units\": 3},{ \"topic\": \"hour\", \"units\": 4},{ \"topic\": \"day\", \"units\": 5}] }";
    private String jsonTestString6Rentals = "{\"topic\": \"family\",\"rentals\": [{ \"topic\": \"day\", \"units\": 1},{ \"topic\": \"week\", \"units\": 2},{ \"topic\": \"day\", \"units\": 3},{ \"topic\": \"hour\", \"units\": 4},{ \"topic\": \"day\", \"units\": 5},{ \"topic\": \"day\", \"units\": 6}] }";
    private String jsonTestStringMissingKey = "{\"family\": \"topic\",\"rentals\": [{ \"topic\": \"day\", \"units\": 1},{ \"topic\": \"week\", \"units\": 2},{ \"topic\": \"day\", \"units\": 3},{ \"topic\": \"hour\", \"units\": 4},{ \"topic\": \"day\", \"units\": 5},{ \"topic\": \"day\", \"units\": 6}] }";
    private String jsonTestStringMissingKey2 = "{\"topic\": \"family\",\"day\": [{ \"topic\": \"day\", \"units\": 1},{ \"topic\": \"week\", \"units\": 2},{ \"topic\": \"day\", \"units\": 3},{ \"topic\": \"hour\", \"units\": 4},{ \"topic\": \"day\", \"units\": 5},{ \"topic\": \"day\", \"units\": 6}] }";
    private String jsonTestStringSingleRental = "{ \"topic\": \"day\", \"units\": 1}";

    private JsonObject setUpMessage(String s) {
        return new JsonParser().
                parse(s)
                .getAsJsonObject();
    }

    @Test
    public void validateApiParameterStructureCompositeSize5(){
        assertTrue(RentalUtils.validateRentalParams(setUpMessage(jsonTestString5Rentals)));
    }

    @Test
    public void validateApiParameterStructureAtomicSize1(){
        assertTrue(RentalUtils.validateRentalParams(setUpMessage(jsonTestStringSingleRental)));
    }

    @Test
    public void validateApiParameterStructureCompositeSize6(){
        assertThrows(RuntimeException.class, () -> {
            RentalUtils.validateRentalParams(setUpMessage(jsonTestString6Rentals));
        });
    }

    @Test
    public void validateApiParameterStructureCompositeSize2(){
        assertThrows(RuntimeException.class, () -> {
            RentalUtils.validateRentalParams(setUpMessage(jsonTestString2Rentals));
        });
    }

    @Test
    public void validateApiParameterStructureMissingParams(){
        assertThrows(RuntimeException.class, () -> {
            RentalUtils.validateRentalParams(setUpMessage(jsonTestStringMissingKey));
        });
    }

    @Test
    public void validateApiParameterStructureMissingParams2(){
        assertThrows(RuntimeException.class, () -> {
            RentalUtils.validateRentalParams(setUpMessage(jsonTestStringMissingKey2));
        });
    }
}
