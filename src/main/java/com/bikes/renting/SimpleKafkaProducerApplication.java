package com.bikes.renting;


import com.bikes.renting.model.iface.Rental;
import com.bikes.renting.model.impl.RentalFactory;
import com.bikes.renting.model.message_engine.consumer.KafkaConsumerFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;

import static com.bikes.renting.model.RentalTypes.TOPICS;

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
            runKafkaConsumer(simpleKafkaConsumer);
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
    private void runKafkaConsumer(KafkaConsumer<String, String> kafkaConsumer) {

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
                    JsonObject receivedJsonObject  = new JsonParser()
                            .parse(message)
                            .getAsJsonObject();
                    /*
                     * To make sure we successfully deserialized the message to a JSON object, we'll
                     * log the index of JSON object.
                     */
                    logger.info("Deserialized JSON object: " + receivedJsonObject);

                    Rental rental = RentalFactory.createRental(receivedJsonObject);
                    logger.info("Cost of rental: " + rental.calculateRentalPricing());
                    //TODO: Here we should store rental information (persist).
                }
            }
        } finally {
            kafkaConsumer.close();
        }
    }
}