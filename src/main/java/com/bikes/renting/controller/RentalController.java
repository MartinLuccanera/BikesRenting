package com.bikes.renting.controller;

import com.google.gson.JsonObject;
import org.apache.catalina.connector.Response;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.bikes.renting.controller.ParametersConstants.*;
import static com.bikes.renting.model.message_engine.JsonKeyConstants.RENTAL_TYPE_JSON_KEY;

//TODO: How to test https://reversecoding.net/spring-mvc-requestparam-binding-request-parameters/

/**
 * <p>Endpoint to post bike rental information and/or obtain rental pricing.</p>
 */
@RestController
@org.springframework.stereotype.Controller
@RequestMapping("/bike")
@ResponseStatus(HttpStatus.OK)
public class RentalController {
    private static final Logger logger = Logger.getLogger(RentalController.class);

    /**
     * <p>Endpoint to store calculations into persistent resource.
     * Once kafka message reaches {@link org.apache.kafka.clients.consumer.KafkaConsumer} it will be persisted.</p>
     * <p>Rental types are also used as kafka topics.</p>
     *
     * @param rentalType Atomic rental type which also is used as kafka topic {@link com.bikes.renting.model.RentalTypes}.
     * @param quantity Amount of hours/days/weeks for the rental type.
     */
    @RequestMapping(
            value = "/rental",
            method = RequestMethod.POST,
            params = {RENTAL_TYPE_PARAMETER_KEY, RENTALS_QUANTITY_PARAMETER_KEY}, // To allow overloaded methods based on parameters
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    ResponseEntity<String> saveBikeRental(
            @RequestParam(name = RENTAL_TYPE_PARAMETER_KEY) String rentalType,
            @RequestParam(name = RENTALS_QUANTITY_PARAMETER_KEY) int quantity)
    {

        /* NOTE:
         * Here we should check for params format, validity, quantity, etc.
         * For simplicity's sake I'll just avoid said checks.
         * NOTE2:
         * Some of those checks have been implemented in RentalUtils.
         */

        //Assembling the message that will be sent to Kafka.
        JsonObject payload = KafkaUtils.assembleMessage(rentalType, quantity);

        // If params are not valid, exceptions will stop execution.
        RentalUtils.validateRentalParams(payload);

        KafkaUtils.sendKafkaMessage(
                payload.toString(),
                payload.getAsJsonPrimitive(RENTAL_TYPE_JSON_KEY).getAsString()
        );

        logger.debug("Message " + payload.toString() + " sent.");

        return new ResponseEntity<>("Sent message: \n" + payload.toString(),
                HttpStatus.OK);
    }

    /**
     * <p>Endpoint to store calculations into persistent resource.
     * Once kafka message reaches {@link org.apache.kafka.clients.consumer.KafkaConsumer} it will be persisted.</p>
     * <p>Rental types are also used as kafka topics.</p>
     *
     * @param composedTopicType Should be a composed kind of rental {@link com.bikes.renting.model.RentalTypes}.
     *                          Also represents a kafka topic.
     * @param rentalTypes List of atomic rentals. Each represents a rental type and a kafka topic {@link com.bikes.renting.model.RentalTypes}.
     * @param quantities Amount of hours/days/weeks for the rental type.
     */
    @RequestMapping(
            value = "/rental",
            method = RequestMethod.POST,
            params = {RENTAL_TYPE_PARAMETER_KEY, NESTED_RENTALS_PARAMETER_KEY, RENTALS_QUANTITY_PARAMETER_KEY}, // To allow overloaded methods based on parameters
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    ResponseEntity<String> saveBikeRental(
            @RequestParam (name = RENTAL_TYPE_PARAMETER_KEY) String composedTopicType,
            @RequestParam (name = NESTED_RENTALS_PARAMETER_KEY, required = false) List<String> rentalTypes,
            @RequestParam (name = RENTALS_QUANTITY_PARAMETER_KEY) List<Integer> quantities)
    {

        /* NOTE:
         * Here we should check for params format, validity, quantity, etc.
         * For simplicity's sake I'll just avoid said checks.
         * NOTE2:
         * Some of those checks have been implemented in RentalUtils.
         */

        //Assembling the message that will be sent to Kafka.
        JsonObject payload = KafkaUtils.assembleMessage(composedTopicType, rentalTypes, quantities);

        // If params are not valid, exception will stop execution.
        RentalUtils.validateRentalParams(payload);

        KafkaUtils.sendKafkaMessage(
                payload.toString(),
                payload.getAsJsonPrimitive(RENTAL_TYPE_JSON_KEY).getAsString()
        );

        logger.debug("Message " + payload.toString() + " sent.");
        return new ResponseEntity<>("Sent message: \n" + payload.toString(),
                HttpStatus.OK);
    }
}