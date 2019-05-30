package com.bikes.renting;


import com.bikes.renting.model.iface.Rental;
import com.bikes.renting.model.impl.RentalFactory;
import com.bikes.renting.model.message_engine.consumer.KafkaConsumerFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;

import static com.bikes.renting.model.PricingConstants.FAMILY_MAX_SIZE;
import static com.bikes.renting.model.PricingConstants.FAMILY_MIN_SIZE;
import static com.bikes.renting.model.RentalTypes.RENTAL_TYPE_FAMILY;
import static com.bikes.renting.model.RentalTypes.TOPICS;
import static com.bikes.renting.model.message_engine.JsonKeyConstants.NESTED_RENTALS_JSON_KEY;
import static com.bikes.renting.model.message_engine.JsonKeyConstants.RENTAL_TYPE_JSON_KEY;

@SpringBootApplication
public class SimpleKafkaProducerApplication implements CommandLineRunner {
    private static final Logger logger = Logger.getLogger(SimpleKafkaProducerApplication.class);

    /**
     * <p>Starts the application.
     * -> Creates consumers on all topics who wait until there are messages to read.
     * When API endpoint is hit {@link com.bikes.renting.controller.RentalController}, producer(s) are created and
     * messages are sent to kafka.</p>
     *
     * @param args nothing special, regular java boilerplate code.
     */
    public static void main( String[] args ) {
        SpringApplication.run(SimpleKafkaProducerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        /*
         * Creating a thread to listen to the kafka topic
         */
        Thread kafkaConsumerThread = new Thread(() -> {
            logger.info("Starting Kafka consumer thread.");
            KafkaConsumer<String, String> simpleKafkaConsumer = KafkaConsumerFactory.
                    createKafKafkaConsumer(TOPICS);
            runSingleWorker(simpleKafkaConsumer);
        });

        /*
         * Starting consumer thread.
         */
        kafkaConsumerThread.start();

    }

    /**
     * <p>This function will start a single worker thread per topic.
     * After creating the consumer object, we subscribe to a list of Kafka topics in the constructor.</p>
     */
    private void runSingleWorker(KafkaConsumer<String, String> kafkaConsumer) {

        /*
         * We will start an infinite while loop, inside which we'll be listening to
         * new messages in each topic that we've subscribed to.
         */
        try {
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(5000));
                // ConsumerRecords<String, String> records = kafkaConsumer.poll(100);

                /*
                 * Whenever there's a new message in the Kafka topic, we'll get the message in this loop, as
                 * the record object.
                 */
                for (ConsumerRecord<String, String> record : records) {
                    logger.debug("topic = %s, partition = %d, offset = %d,customer = %s, country = %s\n"
                            .concat(record.topic())
                            .concat(String.valueOf(record.partition()))
                            .concat(String.valueOf(record.offset()))
                            .concat(record.key())
                            .concat(record.value())
                    );

                    //Retrieving message.
                    String message = record.value();
                    logger.info("Received message: " + message);
                    try {
                        JSONObject receivedJsonObject = new JSONObject(message);
                        /*
                         * To make sure we successfully deserialized the message to a JSON object, we'll
                         * log the index of JSON object.
                         */
                        logger.info("Index of deserialized JSON object: " + receivedJsonObject.getInt("index"));
                    } catch (JSONException e) {
                        logger.error(e.getMessage());
                    }

                    validateJsonAndPrint(message);
                }
            }
        } finally {
            kafkaConsumer.close();
        }
    }

    /**
     * Runs some validations on payload received from kafka consumer.
     * If everything is ok, prints the object and the result of rental calculation.
     *
     * @param message The result of querying kafka queue.
     */
    private void validateJsonAndPrint(String message) {
        JsonObject jsonMessage = new JsonParser().parse(message).getAsJsonObject();
        // Checking for validity of parameter sent through API.

        // It's not a single rental nor a composed rental.
        if (jsonMessage.get(RENTAL_TYPE_JSON_KEY) == null || jsonMessage.get(NESTED_RENTALS_JSON_KEY) == null) {
            String error = "Payload is missing mandatory field." + jsonMessage.toString();
            logger.error(error);
            throw new RuntimeException(error);
        }

        // Here we will handle single rentals.
        if (!jsonMessage.get(RENTAL_TYPE_JSON_KEY).toString().equals(RENTAL_TYPE_FAMILY)) {
            //TODO: Should really check for rental types being only atomic and not composed.
            String msg = "Payload is not composite rental." + jsonMessage.toString();
            logger.info(msg);

            Rental rental = RentalFactory.createRental(jsonMessage);
            logger.info("Cost of rental: " + rental.calculateRentalPricing());
        }

        // Here we will handle composed rentals
        JsonArray rentals = jsonMessage.getAsJsonArray(NESTED_RENTALS_JSON_KEY);

        // Composed rentals have min and max quantities.
        if (rentals.size() > FAMILY_MAX_SIZE || rentals.size() < FAMILY_MIN_SIZE) {
            String error = "Payload has an incorrect number of rentals." + jsonMessage.toString();
            logger.error(error);
            throw new RuntimeException(error);
        }

        //if we got to this point, then we are dealing with a properly formatted Json to calculate
        // a composed type of rental

        String msg = "Payload is a composite rental." + jsonMessage.toString();
        logger.info(msg);

        Rental rental = RentalFactory.createRental(jsonMessage);
        logger.info("Cost of rental: " + rental.calculateRentalPricing());
    }
}