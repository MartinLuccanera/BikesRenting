package com.bikes.renting;


import com.bikes.renting.model.message_engine.consumer.KafkaConsumerFactory;
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
import static com.bikes.renting.model.RentalTypes.TOPICS;

@SpringBootApplication
public class SimpleKafkaProducerApplication implements CommandLineRunner {
    private static final Logger logger = Logger.getLogger(SimpleKafkaProducerApplication.class);

    @Value("${kafka.bootstrap.servers}")
    private String kafkaBootstrapServers;

    @Value("${zookeeper.host}")
    String zookeeperHost;

    public static void main( String[] args ) {
        //TODO: Use this class to create the consumers. Then we create the producer when the first message arrives.
        // Hence, the app will show whatever is sent each time a message arrives and the process will be demonstrated.
        // I need to create all the consumers here. Consumer for family, right?
        SpringApplication.run(SimpleKafkaProducerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        /*
         * Creating a thread to listen to the kafka topic
         */
        Thread kafkaConsumerThread = new Thread(() -> {
            logger.info("Starting Kafka consumer thread.");
            //KafkaProducer<String, String> kafkaProducer = KafkaProducerFactory.createKafKafkaProducer();
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

        //TODO: Should receive the messages and calculate + show rental price.

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
                    /*
                     * Getting the message as a string from the record object.
                     */
                    String message = record.value();
                    /*
                     * Logging the received message to the console.
                     */
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
                    /*
                    //offset commit is automatic, no need to do manually.
                    Map<TopicPartition, OffsetAndMetadata> commitMessage = new HashMap<>();
                    commitMessage.put(new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1));

                    kafkaConsumer.commitSync(commitMessage);

                    logger.info("Offset committed to Kafka.");
                    */
                }
            }
        } finally {
            kafkaConsumer.close();
        }
    }
}