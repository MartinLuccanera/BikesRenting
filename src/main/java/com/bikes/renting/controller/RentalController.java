package com.bikes.renting.controller;

import com.bikes.renting.SimpleKafkaProducerApplication;
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
    private static final Logger logger = Logger.getLogger(SimpleKafkaProducerApplication.class);

    /**
     * <p>Endpoint to store calculations into persistent resource.
     * Once kafka message reaches {@link org.apache.kafka.clients.consumer.KafkaConsumer} it will be persisted.</p>
     * <p>Rental types are also used as kafka topics.</p>
     *
     * @param rentalType Atomic rental type which also is used as kafka topic {@link com.bikes.renting.model.RentalTypes}.
     * @param quantity Amount of hours/days/weeks for the rental type.
     *
     * @return This return is NOT a production kind of return but a way to assert endpoint reach.
     */
    @RequestMapping(
            value = "/rental",
            method = RequestMethod.POST,
            params = {"rentalType", "quantity"}, // To allow overloaded methods based on parameters
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    String saveBikeRental(
            @RequestParam(name = "rentalType") String rentalType,
            @RequestParam(name = "quantity") int quantity)
    {
        KafkaProducer<String, String> kafkaProducer = createKafKafkaProducer();
        logger.debug("producer create" + kafkaProducer.toString());

        /* NOTE:
         * Here we should check for params format, validity, quantity, etc.
         * For simplicity's sake I'll just avoid said checks.
         */

        //Assembling the message that will be sent to Kafka.
        JsonObject payload = assembleMessage(rentalType, quantity);

        sendKafkaMessage(
                payload.toString(),
                kafkaProducer,
                payload.getAsJsonPrimitive(RENTAL_TYPE_JSON_KEY).getAsString()
        );
        logger.debug("Message " + payload.toString() + " sent.");
        return "Received message: " + payload;
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
     *
     * @return This return is NOT a production kind of return but a way to assert endpoint reach.
     */
    @RequestMapping(
            value = "/rental",
            method = RequestMethod.POST,
            params = {"rentalType", "subRental", "quantity"}, // To allow overloaded methods based on parameters
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    String saveBikeRental(
            @RequestParam (name = "rentalType") String composedTopicType,
            @RequestParam (name = "subRental", required = false) List<String> rentalTypes,
            @RequestParam (name = "quantity") List<String> quantities)
    {
        KafkaProducer<String, String> kafkaProducer = createKafKafkaProducer();
        logger.debug("producer create" + kafkaProducer.toString());

        /* NOTE:
         * Here we should check for params format, validity, quantity, etc.
         * For simplicity's sake I'll just avoid said checks.
         */

        //Assembling the message that will be sent to Kafka.
        JsonObject payload = assembleMessage(composedTopicType, rentalTypes, quantities);

        sendKafkaMessage(
                payload.toString(),
                kafkaProducer,
                payload.getAsJsonPrimitive(RENTAL_TYPE_JSON_KEY).getAsString()
        );

        logger.debug("Message " + payload.toString() + " sent.");
        return "Received message: " + payload;
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
     *
     * @return This return is NOT a production kind of return but a way to assert endpoint reach.
     */
    @RequestMapping(
            value = "/rental",
            method = RequestMethod.GET,
            params = {"rentalType", "subRental", "quantity"}, // To allow overloaded methods based on parameters
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getResponse(
            @RequestParam (name = "rentalType") String composedTopicType,
            @RequestParam (name = "subRental") List<String> rentalTypes,
            @RequestParam (name = "quantity") List<String> quantities)
    {
        JsonObject payload = assembleMessage(composedTopicType, rentalTypes, quantities);

        KafkaProducer<String, String> kafkaProducer = createKafKafkaProducer();
        sendKafkaMessage(payload.toString(), kafkaProducer, payload.getAsJsonPrimitive(RENTAL_TYPE_JSON_KEY).getAsString());

        return " Got to endpoint + " + payload;
    }

    /**
     * <p>Endpoint for testing calculations.</p>
     *
     * @param rentalType Atomic rental representing a rental type and a kafka topic {@link com.bikes.renting.model.RentalTypes}.
     * @param quantity Amount of hours/days/weeks for the rental type.
     * @return This return is NOT a production kind of return but a way to assert endpoint reach.
     */
    @RequestMapping(
            value = "/rental",
            method = RequestMethod.GET,
            params = {"rentalType", "quantity"}, // To allow overloaded methods based on parameters
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getResponse(
            @RequestParam(name = "rentalType") String rentalType,
            @RequestParam(name = "quantity") int quantity)
    {
        JsonObject payload = assembleMessage(rentalType, quantity);

        KafkaProducer<String, String> kafkaProducer = createKafKafkaProducer();

        sendKafkaMessage(
                payload.toString(),
                kafkaProducer,
                payload.getAsJsonPrimitive(RENTAL_TYPE_JSON_KEY).getAsString()
        );
        logger.debug("Message " + payload.toString() + " sent.");
        return " Got to endpoint + " + payload;
    }

    /**
     * <p>Function to send messages to kafka. Receives producer (sender) and message as parameters</p>
     *
     * @param payload Message to be sent via {@link KafkaProducer}.
     * @param producer Kafka producer (sender). Serves as entry point to kafka.
     * @param topic Kafka topic in which we are gonna send this message {@link com.bikes.renting.model.RentalTypes}
     */
    private void sendKafkaMessage(String payload, KafkaProducer<String, String> producer,String topic) {
        logger.info("Sending Kafka message: " + payload);
        producer.send(new ProducerRecord<>(topic, payload));
    }
}