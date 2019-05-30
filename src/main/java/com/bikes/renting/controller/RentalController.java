package com.bikes.renting.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;

import static com.bikes.renting.controller.ParametersConstants.NESTED_RENTALS_PARAMETER_KEY;
import static com.bikes.renting.controller.ParametersConstants.RENTALS_QUANTITY_PARAMETER_KEY;
import static com.bikes.renting.controller.ParametersConstants.RENTAL_TYPE_PARAMETER_KEY;
import static com.bikes.renting.model.message_engine.JsonKeyConstants.NESTED_RENTALS_JSON_KEY;
import static com.bikes.renting.model.message_engine.JsonKeyConstants.RENTALS_QUANTITY_JSON_KEY;
import static com.bikes.renting.model.message_engine.JsonKeyConstants.RENTAL_TYPE_JSON_KEY;
import static com.bikes.renting.model.message_engine.producer.KafkaProducerFactory.*;

//TODO: How to test https://reversecoding.net/spring-mvc-requestparam-binding-request-parameters/

/**
 * <p>Endpoint to post bike rental information and/or obtain rental pricing.</p>
 */
@RestController
@RequestMapping("/bike")
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
    void saveBikeRental(
            @RequestParam(name = RENTAL_TYPE_PARAMETER_KEY) String rentalType,
            @RequestParam(name = RENTALS_QUANTITY_PARAMETER_KEY) int quantity)
    {

        /* NOTE:
         * Here we should check for params format, validity, quantity, etc.
         * For simplicity's sake I'll just avoid said checks.
         */

        //Assembling the message that will be sent to Kafka.
        JsonObject payload = assembleMessage(rentalType, quantity);

        // If params are not valid, exception will stop execution.
        RentalUtils.validateRentalParams(payload);

        sendKafkaMessage(
                payload.toString(),
                payload.getAsJsonPrimitive(RENTAL_TYPE_JSON_KEY).getAsString()
        );
        logger.debug("Message " + payload.toString() + " sent.");
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
    void saveBikeRental(
            @RequestParam (name = RENTAL_TYPE_PARAMETER_KEY) String composedTopicType,
            @RequestParam (name = NESTED_RENTALS_PARAMETER_KEY, required = false) List<String> rentalTypes,
            @RequestParam (name = RENTALS_QUANTITY_PARAMETER_KEY) List<String> quantities)
    {

        /* NOTE:
         * Here we should check for params format, validity, quantity, etc.
         * For simplicity's sake I'll just avoid said checks.
         */

        //Assembling the message that will be sent to Kafka.
        JsonObject payload = assembleMessage(composedTopicType, rentalTypes, quantities);

        // If params are not valid, exception will stop execution.
        RentalUtils.validateRentalParams(payload);

        sendKafkaMessage(
                payload.toString(),
                payload.getAsJsonPrimitive(RENTAL_TYPE_JSON_KEY).getAsString()
        );

        logger.debug("Message " + payload.toString() + " sent.");
    }

    /**
     * <p>Given request parameters, assembles a {@link JsonObject} message to send to kafka.</p>
     * <p>Overloaded method for the generation of nested rentals.</p>
     *
     * @param composedTopicType Should be a composed-topic kind of topic {@link com.bikes.renting.model.RentalTypes}.
     * @param rentalTypes List of atomic rentals. Each represents a rental type and a kafka topic {@link com.bikes.renting.model.RentalTypes}.
     * @param quantities Amount of hours/days/weeks for the rental type.
     *
     * @return Kafka-ready message as {@link JsonObject}.
     */
    private JsonObject assembleMessage(String composedTopicType, List<String> rentalTypes, List<String> quantities) {
        //Here we create a JsonObject that contains a JsonArray of JsonObjects. For more info refer to README.md

        //payload is the message we will send to kafka as a JsonObject
        JsonObject payload = new JsonObject();
        payload.addProperty(RENTAL_TYPE_JSON_KEY, composedTopicType);

        JsonArray subRentalsArray = new JsonArray();
        JsonObject subRental = new JsonObject();

        Iterator i1 = rentalTypes.iterator();
        Iterator i2 = quantities.iterator();

        //We read through both lists of parameters and put them together in a single JsonObject
        while (i1.hasNext() && i2.hasNext()) {
            subRental.addProperty(
                    i1.next().toString(),
                    i2.next().toString()
            );

            subRentalsArray.add(subRental);
            subRental = new JsonObject();
        }

        payload.add(NESTED_RENTALS_JSON_KEY, subRentalsArray);
        return payload;
    }

    /**
     * <p>Given request parameters, assembles a {@link JsonObject} message to send to kafka.</p>
     * <p>Overloaded method for the generation of simple rentals.</p>
     *
     * @param rentalType Atomic rental type which also is used as kafka topic {@link com.bikes.renting.model.RentalTypes}.
     * @param quantity Amount of hours/days/weeks for the rental type.
     *
     * @return @return Kafka-ready message as {@link JsonObject}.
     */
    private JsonObject assembleMessage(String rentalType, int quantity) {
        JsonObject familyRental = new JsonObject();
        familyRental.addProperty(RENTAL_TYPE_JSON_KEY, rentalType);
        familyRental.addProperty(RENTALS_QUANTITY_JSON_KEY, quantity);
        return familyRental;
    }

    /**
     * <p>Endpoint for testing calculations.</p>
     *
     * @param composedTopicType Should be a composed kind of rental {@link com.bikes.renting.model.RentalTypes}.
     *                          Also represents a kafka topic.
     * @param rentalTypes List of atomic rentals. Each represents a rental type and a kafka topic {@link com.bikes.renting.model.RentalTypes}.
     * @param quantities Amount of hours/days/weeks for the rental type.
     */
    @RequestMapping(
            value = "/rental",
            method = RequestMethod.GET,
            params = {RENTAL_TYPE_PARAMETER_KEY, NESTED_RENTALS_PARAMETER_KEY, RENTALS_QUANTITY_PARAMETER_KEY}, // To allow overloaded methods based on parameters
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void getResponse(
            @RequestParam (name = RENTAL_TYPE_PARAMETER_KEY) String composedTopicType,
            @RequestParam (name = NESTED_RENTALS_PARAMETER_KEY) List<String> rentalTypes,
            @RequestParam (name = RENTALS_QUANTITY_PARAMETER_KEY) List<String> quantities)
    {
        JsonObject payload = assembleMessage(composedTopicType, rentalTypes, quantities);

        // If params are not valid, exception will stop execution.
        RentalUtils.validateRentalParams(payload);

        sendKafkaMessage(
                payload.toString(),
                payload.getAsJsonPrimitive(RENTAL_TYPE_JSON_KEY).getAsString()
        );
    }

    /**
     * <p>Endpoint for testing calculations.</p>
     *
     * @param rentalType Atomic rental representing a rental type and a kafka topic {@link com.bikes.renting.model.RentalTypes}.
     * @param quantity Amount of hours/days/weeks for the rental type.
     */
    @RequestMapping(
            value = "/rental",
            method = RequestMethod.GET,
            params = {RENTAL_TYPE_PARAMETER_KEY, RENTALS_QUANTITY_PARAMETER_KEY}, // To allow overloaded methods based on parameters
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void getResponse(
            @RequestParam(name = RENTAL_TYPE_PARAMETER_KEY) String rentalType,
            @RequestParam(name = RENTALS_QUANTITY_PARAMETER_KEY) int quantity)
    {
        JsonObject payload = assembleMessage(rentalType, quantity);

        // If params are not valid, exception will stop execution.
        RentalUtils.validateRentalParams(payload);

        sendKafkaMessage(
                payload.toString(),
                payload.getAsJsonPrimitive(RENTAL_TYPE_JSON_KEY).getAsString()
        );
    }

    /**
     * <p>Function to send messages to kafka. Receives producer (sender) and message as parameters</p>
     *
     * @param payload Message to be sent via {@link KafkaProducer}.
     * @param topic Kafka topic in which we are gonna send this message {@link com.bikes.renting.model.RentalTypes}
     */
    private void sendKafkaMessage(String payload, String topic) {
        //Singleton creator.
        KafkaProducer<String, String> kafkaProducer = createKafKafkaProducer();
        logger.debug("producer created \n" + kafkaProducer.toString());
        logger.info("Sending Kafka message: \n" + payload);

        kafkaProducer.send(new ProducerRecord<>(topic, payload));
    }
}