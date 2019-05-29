package com.bikes.renting;


import com.bikes.renting.model.message_engine.consumer.SimpleKafkaConsumer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;
import static com.bikes.renting.model.RentalTypes.TOPICS;

@SpringBootApplication
public class SimpleKafkaProducerApplication implements CommandLineRunner {

    @Value("${kafka.bootstrap.servers}")
    private String kafkaBootstrapServers;

    @Value("${zookeeper.host}")
    String zookeeperHost;

    @Value("${zookeeper.groupId}")
    String zookeeperGroupId;

    private static final Logger logger = Logger.getLogger(SimpleKafkaProducerApplication.class);

    public static void main( String[] args ) {
        //TODO: Use this class to create the consumers. Then we create the producer when the first message arrives.
        // Hence, the app will show whatever is sent each time a message arrives and the process will be demonstrated.
        // I need to create all the consumers here. Consumer for family, right?
        SpringApplication.run(SimpleKafkaProducerApplication.class, args);
    }

    @Override
    public void run(String... args) {

        /*
         * Defining Kafka consumer properties.
         */
        Properties consumerProperties = new Properties();
        consumerProperties.put("bootstrap.servers", kafkaBootstrapServers);
        consumerProperties.put("group.id", zookeeperGroupId);
        consumerProperties.put("auto.commit.interval.ms", "1000");
        consumerProperties.put("reconnect.backoff.ms", "5000");
        consumerProperties.put("auto.offset.reset", "earliest");
        consumerProperties.put("max.poll.records", "1");
        consumerProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        /*
         * Creating a thread to listen to the kafka topic
         */
        Thread kafkaConsumerThread = new Thread(() -> {
            logger.info("Starting Kafka consumer thread.");

            SimpleKafkaConsumer simpleKafkaConsumer = new SimpleKafkaConsumer(
                    TOPICS,
                    consumerProperties
            );

            simpleKafkaConsumer.runSingleWorker();
        });

        /*
         * Starting the first thread.
         */
        kafkaConsumerThread.start();
    }
}