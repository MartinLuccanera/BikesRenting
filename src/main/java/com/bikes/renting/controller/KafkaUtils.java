package com.bikes.renting.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;

import static com.bikes.renting.model.message_engine.JsonKeyConstants.NESTED_RENTALS_JSON_KEY;
import static com.bikes.renting.model.message_engine.JsonKeyConstants.RENTALS_QUANTITY_JSON_KEY;
import static com.bikes.renting.model.message_engine.JsonKeyConstants.RENTAL_TYPE_JSON_KEY;
import static com.bikes.renting.model.message_engine.producer.KafkaProducerFactory.createKafKafkaProducer;

public class KafkaUtils {
    private static final Logger logger = Logger.getLogger(KafkaUtils.class);

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
    public static JsonObject assembleMessage(String composedTopicType, List<String> rentalTypes, List<Integer> quantities) {
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
                    RENTAL_TYPE_JSON_KEY,
                    i1.next().toString()
            );
            subRentalsArray.add(subRental);
            subRental.addProperty(
                    RENTALS_QUANTITY_JSON_KEY,
                    Integer.valueOf(i2.next().toString())
            );
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
    public static JsonObject assembleMessage(String rentalType, int quantity) {
        JsonObject familyRental = new JsonObject();
        familyRental.addProperty(RENTAL_TYPE_JSON_KEY, rentalType);
        familyRental.addProperty(RENTALS_QUANTITY_JSON_KEY, quantity);
        return familyRental;
    }


    /**
     * <p>Function to send messages to kafka. Receives producer (sender) and message as parameters</p>
     *
     * @param payload Message to be sent via {@link KafkaProducer}.
     * @param topic Kafka topic in which we are gonna send this message {@link com.bikes.renting.model.RentalTypes}
     */
    public static void sendKafkaMessage(String payload, String topic) {
        //Singleton creator.
        KafkaProducer<String, String> kafkaProducer = createKafKafkaProducer();
        logger.debug("producer created \n" + kafkaProducer.toString());
        logger.info("Sending Kafka message: \n" + payload);

        kafkaProducer.send(new ProducerRecord<>(topic, payload));
    }
}
